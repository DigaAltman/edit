package cn.bbzzzs.db.factory;

import cn.bbzzzs.common.util.ReflexUtils;
import cn.bbzzzs.db.result.bean.Result;
import cn.bbzzzs.db.result.bean.ResultMap;
import cn.bbzzzs.db.result.factory.ResultMapFactory;
import cn.bbzzzs.db.result.handler.ResultRowHandler;
import cn.bbzzzs.db.result.handler.support.DefaultResultRowHandler;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class DB {
    // 连接对象
    @Getter
    private Connection connection;

    // 结果集映射容器工厂
    private ResultMapFactory factory;

    // 行结果处理器
    private ResultRowHandler rowHandler;

    public DB(Connection connection, ResultMapFactory factory) {
        this.connection = connection;
        this.factory = factory;
        this.rowHandler = new DefaultResultRowHandler(factory);
    }

    private PreparedStatement buildPrepareStatement(String sql, Object... args) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);

            for (int i = 1; i <= args.length; i++) {
                preparedStatement.setObject(i, args[i - 1]);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return preparedStatement;
    }


    /**
     * 将当前行的 resultSet 转换为 Map
     *
     * @param resultSet
     * @return
     */
    private LinkedHashMap resultSetToMap(ResultSet resultSet) throws SQLException {
        LinkedHashMap<String, Object> map = new LinkedHashMap();
        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnLabel(i);
            Object value = resultSet.getObject(columnName);
            map.put(columnName, value);
        }
        return map;
    }


    public <T> List<T> selectList(String sql, ResultMap resultMap, Object... args) {
        List<T> result = new ArrayList();
        // 判断当前的 sql 是否为null, 或者是空字符串
        if (sql != null && !sql.trim().equals("")) {
            try {
                // 获取原生的 ResultSet 返回结果
                ResultSet resultSet = buildPrepareStatement(sql, args).executeQuery();

                // 循环进行 一对一映射 处理
                while (resultSet.next()) {
                    LinkedHashMap returnMap = resultSetToMap(resultSet);
                    result.add(rowHandler.handle(returnMap, resultMap));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


            // 开始处理一对多映射： 记录处理过的ResultMap, 用于作为终止条件. 为什么不用Set, 因为我们还需要做一次倒序处理
            result = oneToMany(result, resultMap, new LinkedList());

        }


        return result;
    }

    /**
     * 查询多条数据库记录并做处理
     *
     * @param sql  查询语句
     * @param args 查询语句需要使用到的参数
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> selectList(String sql, Class<T> returnClass, Object... args) {
        List<T> result = new ArrayList();
        // 判断当前的 sql 是否为null, 或者是空字符串
        if (sql != null && !sql.trim().equals("")) {
            // 获取原生的 ResultSet 返回结果
            try {
                ResultSet resultSet = buildPrepareStatement(sql, args).executeQuery();

                // 循环进行 一对一映射 处理
                while (resultSet.next()) {
                    result.add(rowHandler.handle(resultSetToMap(resultSet), returnClass));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


            if (
                    !Map.class.isAssignableFrom(returnClass) &&
                    !Set.class.isAssignableFrom(returnClass) &&
                    !Object[].class.isAssignableFrom(returnClass) &&
                    !List.class.isAssignableFrom(returnClass) &&
                    !ResultMap.basicTypeSet.contains(returnClass)
            ) {
                // 开始处理一对多映射： 记录处理过的ResultMap, 用于作为终止条件. 为什么不用Set, 因为我们还需要做一次倒序处理
                result = oneToMany(result, ResultMap.generate(factory, returnClass), new LinkedList());
            }
        }


        return result;
    }

    private static <T> List<T> oneToMany(List<T> list, ResultMap resultMap, List<ResultMap> alreadyReadMapList) {
        if (!alreadyReadMapList.contains(resultMap)) {
            alreadyReadMapList.add(resultMap);
        } else {
            return list;
        }

        List res = null;

        List<Result> childResult = resultMap.childMapResult();
        for (Result el : childResult) {
            boolean one = false;
            ResultMap map = el.getCollectionMap();

            // 判断对应关系, resultMap 和 map 之间的对应关系是 一对一 还是 一对多
            if (map == null) {
                one = true;
                map = el.getAssociationMap();
            }

            List childList = new ArrayList();
            // 循环处理每一行数据
            for (Object t : list) {
                Object child = ReflexUtils.get(t, el.getProperty());

                if (el.getCollectionMap() != null) {
                    List objectList = (List) child;
                    if (objectList.size() > 0) {
                        childList.add(objectList.get(0));
                    }
                } else {
                    childList.add(child);
                }

            }

            res = oneToMany(childList, map, alreadyReadMapList);


            // 然后再将
            // 如果是一对一, 那么我们就将 Teacher 和 Course 的值合并在一起,作为一个key,
            if (one) {
                List<String> fieldList = map.fieldId();

                // 这个 objectMap 用来存放 一对一 映射中的子方的.
                Map<String, Object> objectMap = new HashMap();

                // 这一步的打算就是计算出每个 Course 的值, 以及对应的 Course 对象
                for (Object child : res) {
                    if (child != null) {
                        String key = values(child, fieldList);
                        if (!objectMap.containsKey(key)) {
                            objectMap.put(key, child);
                        }
                    }

                }


                // 让 Teacher 中的 Course 字段 指向 同一个 Object 引用
                for (Object teacher : list) {
                    Object course = ReflexUtils.get(teacher, el.getProperty());

                    if (course != null) {
                        String key = values(course, fieldList);
                        ReflexUtils.set(teacher, el.getProperty(), objectMap.get(key));
                    }
                }

                // 此时, Teacher 下的 Course 引用已经解决了. 现在要解决的就是不同的 Teacher
                Set<String> teacherSet = new HashSet();

                // 保证顺序性
                List<Object> resultList = new ArrayList();

                List<String> fList = resultMap.fieldId();
                for (Object teacher : list) {
                    String key = values(teacher, fList);
                    if (!teacherSet.contains(key)) {
                        teacherSet.add(key);
                        resultList.add(teacher);
                    }
                }


                // 最后一步, 将 Teacher 的值进行一个整合, 然后返回
                return resultList.stream().map(bean -> (T) bean).collect(Collectors.toList());
            }

            // 如果是一对多, 我们要做的事情就是首先还是要过滤一遍Teacher,
            else {
                if (res.size() > 0) {
                    // 定义一个存放一方数据的Map, key就是这个一方的数据的唯一表示
                    List<Object> oneList = new ArrayList();
                    Set<String> oneKeySet = new HashSet();

                    // 定义一个存放多方数据的Map, key就是这个一方数据的唯一表示
                    Map<String, List> tMap = new HashMap();

                    // 获取当前resultMap中的id字段,如果没用定义id,则取不涉及映射的所有字段组成作为一个id大字段
                    List<String> fList = resultMap.fieldId();

                    // 循环遍历一方的数据,因为多方的数据其实就是一方的数据的子数据的合集, 所以这里一方的数据和多方的数据长度
                    // 是一致的, 以至于我们可以通过同样的 i 索引来获取
                    for (int i = 0; i < list.size(); i++) {
                        Object teacher = list.get(i);

                        List childElements = ReflexUtils.get(teacher, el.getProperty());
                        Object course = null;
                        if (childElements.size() > 0) {
                            course = res.get(i);
                        }

                        // 基于一方的数据和一方的id字段得到一个key, 这个key的内容如下: "id=1&name=张三&"
                        String key = values(teacher, fList);

                        // 如果在这个一方数据的Map中,不存在同样的key了, 我们在添加进去
                        if (!oneKeySet.contains(key)) {
                            oneKeySet.add(key);
                            oneList.add(teacher);
                        }

                        // 从存放多方数据的Map中取出这个key对应的List
                        List courseList = tMap.get(key);

                        // 如果不存在,则创建并添加进去
                        if (courseList == null) {
                            courseList = Lists.newArrayList();
                            tMap.put(key, courseList);
                        }

                        // 这个list再添加多方的数据
                        if (course != null) {
                            courseList.add(course);
                        }
                    }

                    // 将 tMap 和 teacherMap 通过相同的 key 进行合并
                    for (Object o : oneList) {
                        String key = values(o, fList);
                        List courseList = tMap.get(key);

                        // 修改一方中对应的多方的引用
                        ReflexUtils.set(o, el.getProperty(), courseList);

                    }

                    return oneList.stream().map(bean -> (T) bean).collect(Collectors.toList());
                }
            }
        }

        return list;
    }

    /**
     * 根据每个bean中对应的字段得到一个唯一的值
     *
     * @param bean      实例对象
     * @param fieldList 字段集合
     * @return
     */
    private static String values(Object bean, List<String> fieldList) {
        StringBuilder sb = new StringBuilder();
        for (String field : fieldList) {
            try {
                Field f = bean.getClass().getDeclaredField(field);
                f.setAccessible(true);
                sb.append(f.getName()).append("=");
                Object v = f.get(bean);
                if (v == null) {
                    sb.append("null");
                } else {
                    sb.append(v.toString());
                }
                sb.append("&");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public <T> T selectOne(String sql, ResultMap resultMap, Object... args)  {
        try {
            List<T> res = selectList(sql, resultMap, args);
            if (res.size() > 1) {
                throw new RuntimeException("return dataSize allow max = 1");
            }
            return res.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> T selectOne(String sql, Class<T> returnClass, Object... args) {
        List<T> res = selectList(sql, returnClass, args);
        if (res.size() > 1) {
            throw new RuntimeException("return dataSize allow max = 1");
        }
        return res.get(0);
    }

    public boolean execute(String sql, Object... args) {
        try {
            boolean res = buildPrepareStatement(sql, args).execute();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int executeUpdate(String sql, Object... args) {
        try {
            int res = buildPrepareStatement(sql, args).executeUpdate();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
