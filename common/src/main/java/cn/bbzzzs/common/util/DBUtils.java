//package cn.bbzzzs.common.util;
//
//import cn.bbzzzs.common.db.ResultMap;
//import cn.bbzzzs.common.db.ResultMapBuilder;
//import cn.bbzzzs.common.db.ResultRowHandler;
//import com.google.common.collect.Lists;
//import lombok.Getter;
//
//import java.lang.reflect.Field;
//import java.sql.*;
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * @author bbzzzs
// * @description 简化 JDBC 操作的工具类
// */
//public class DBUtils {
//
//    /**
//     * 基于Connection返回一个DB类, 这个DB类很强大
//     * @param connection
//     * @return
//     */
//    public static DB build(Connection connection) {
//        try {
//            return new DB(connection);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    /**
//     * 执行 CRUD 的具体底层类
//     */
//    public static class DB {
//
//        /**
//         * 数据库连接对象, 我们可以使用 代理 的方式,对 connection 进行一个切面
//         */
//        @Getter
//        private Connection connection;
//
//        public DB(Connection connection) {
//            this.connection = connection;
//        }
//
//        /**
//         * 创建 PrepareStatement , 传入 sql 和多个 args. 基于位置进行映射
//         *
//         * 以后我们可以针对 SELECT * FROM Student WHERE name=#{name} AND _version=#{version}
//         *
//         * 我们可以将 @Param('name') String name, @Param('version') Integer version 转换为 {'name', '小明'}， {'version', 1}
//         *
//         * 然后针对这条 sql 语句进行循环替换, 首先找到 #{name} 出现的所有位置,因为可以传入多个 #{name}, 然后组成一个 'name' 的 [],
//         * 然后在找到 #{version} 出现的所有位置, 组成一个 'version' 的集合. 最后一套循环确定 args 的顺序,
//         *
//         * 然后就可以调用这个方法来构建 PreparedStatement 了
//         *
//         * @param sql       预编译的sql
//         * @param args      编译时的参数
//         * @return  返回最终构建的 PrepareStatement
//         */
//        private PreparedStatement buildPrepareStatement(String sql, Object... args) {
//            PreparedStatement preparedStatement = null;
//            try {
//                preparedStatement = connection.prepareStatement(sql);
//
//                for (int i = 1; i <= args.length; i++) {
//                    preparedStatement.setObject(i, args[i - 1]);
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//
//            return preparedStatement;
//        }
//
//        /**
//         * 将当前行的 resultSet 转换为 Map
//         *
//         * @param resultSet
//         * @return
//         */
//        private LinkedHashMap resultSetToMap(ResultSet resultSet) throws SQLException {
//            LinkedHashMap<String, Object> map = new LinkedHashMap();
//            ResultSetMetaData metaData = resultSet.getMetaData();
//            for (int i = 1; i <= metaData.getColumnCount(); i++) {
//                String columnName = metaData.getColumnLabel(i);
//                Object value = resultSet.getObject(columnName);
//                map.put(columnName, value);
//            }
//            return map;
//        }
//
//
//
//        /**
//         * 查询多条数据库记录并做处理
//         *
//         * @param sql              查询语句
//         * @param resultRowHandler 针对每条返回结果的处理逻辑
//         * @param args             查询语句需要使用到的参数
//         * @param <T>
//         * @return
//         * @throws Exception
//         */
//        public <T> List<T> selectList(String sql, ResultRowHandler resultRowHandler, Class<T> returnClass, Object... args) throws Exception {
//            List<T> result = new ArrayList();
//            // 判断当前的 sql 是否为null, 或者是空字符串
//            if (sql != null && !sql.trim().equals("")) {
//                // 获取返回结果
//                ResultSet resultSet = buildPrepareStatement(sql, args).executeQuery();
//
//                // 循环进行处理
//                while (resultSet.next()) {
//                    result.add(resultRowHandler.handle(resultSetToMap(resultSet), returnClass));
//                }
//            }
//
//
//            /*-------------------------------------------------*/
//            /*-------------- 开始处理一对多映射 ------------------*/
//            /*-------------------------------------------------*/
//
//            // 记录处理过的ResultMap, 用于作为终止条件. 为什么不用Set, 因为我们还需要做一次倒序处理
//            result = oneToMany(result, ResultMapBuilder.build(returnClass), new LinkedList());
//
//            return result;
//        }
//
//        /**
//         * 一对多转换的核心转换方法, 我们这里以一个例子讲明：
//         *
//         * 因为这个方法所在处理完一对一映射后, 才调用的
//         *
//         * @lombok.Data
//         * public class Course {
//         *     private Integer courseId;
//         *     private String courseName;
//         *     private List<Student> studentList;
//         * }
//         *
//         * @lombok.Data
//         * public class Student {
//         *     private Integer studentId;
//         *     private String studentName;
//         * }
//         *
//         * SELECT * FROM course c LEFT JOIN student s ON c.course_id = s.course_id WHERE c.course_id = 1
//         *
//         * 此时, 调用这个方法前, 转换的数据如下
//         *      Course@1(1, '马老师', [Student@1(1, '马老师的技霸')]),
//         *      Course@2(1, '马老师', [Student@2(2, '马老师的高玩')]),
//         *      Course@3(1, '马老师', [Student@3(3, '皮燕子')]),
//         *      Course@4(1, '马老师', [Student@4(4, '班花胡莉娅')]),
//         *
//         * 现在在调用这个方法前传入的参数值大致如下:
//         *      list => [
//         *          Course@1(1, '马老师', [Student@1(1, '马老师的技霸')]),
//         *          Course@2(1, '马老师', [Student@2(2, '马老师的高玩')]),
//         *          Course@3(1, '马老师', [Student@3(3, '皮燕子')]),
//         *          Course@4(1, '马老师', [Student@4(4, '班花胡莉娅')]),
//         *      ],
//         *
//         *      resultMap => {
//         *          id: "Course",
//         *          resultType: Course.class,
//         *          resultFieldList => [
//         *              {
//         *                  column: 'course_id',
//         *                  columnClassType: Integer.class,
//         *                  prototype: 'courseId',
//         *                  primaryKey: false,
//         *                  oneMap: null,
//         *                  listMap: null
//         *              },
//         *              {
//         *                  column: 'course_name',
//         *                  columnClassType: String.class,
//         *                  prototype: 'courseName',
//         *                  primaryKey: false,
//         *                  oneMap: null,
//         *                  listMap: null
//         *              },
//         *              {
//         *                  column: 'student_list',
//         *                  columnClassType: List.class,
//         *                  prototype: 'studentList',
//         *                  primaryKey: false,
//         *                  oneMap: null,
//         *                  listMap: {
//         *                      id: "Student",
//         *                      resultType: Student.class,
//         *                      resultFieldList: [
//         *                          {
//         *                               column: 'student_id',
//         *                               columnClassType: Integer.class,
//         *                               prototype: 'studentId',
//         *                               primaryKey: false,
//         *                               oneMap: null,
//         *                               listMap: null
//         *                          },
//         *                          {
//         *                               column: 'student_name',
//         *                               columnClassType: String.class,
//         *                               prototype: 'studentName',
//         *                               primaryKey: false,
//         *                               oneMap: null,
//         *                               listMap: null
//         *                          }
//         *                      ]
//         *                  }
//         *              },
//         *          ]
//         *      },
//         *
//         *      alreadyReadMapList = []
//         *
//         *
//         * 这个参数显示起来有点长,你可以把内容复制到另外一个页面. 慢慢看.
//         * 我们会在下面的代码中基于上传的参数来写一个实时流程
//         *
//         * @param list               处理之前的多条未完成一对多转换的数据
//         * @param resultMap          当前的返回结果处理抽象集合 ResultMap
//         * @param alreadyReadMapList 已经处理中的 ResultMap
//         * @param <T>
//         * @return 返回处理后的数据
//         */
//        private static <T> List<T> oneToMany(List<T> list, ResultMap resultMap, List<ResultMap> alreadyReadMapList) {
//            /*
//             * 判断当前的这个返回结果处理抽象集合是否已经包含在 alreadyReadMapList 中, 如果已经包含. 则不做映射处理了
//             * 直接返回当前传入的未处理的数据集合
//             */
//            /**
//             * step1: alreadyReadMapList = [ResultMap@12(id='Course', resultType=Course.class)]
//             *
//             * step2: alreadyReadMapList = [ResultMap@12(id='Course', resultType=Course.class), ResultMap@13(id='Student', resultType=Student.class)]
//             */
//            if (!alreadyReadMapList.contains(resultMap)) {
//                alreadyReadMapList.add(resultMap);
//            } else {
//                return list;
//            }
//
//            // 循环处理 ResultMap 和 list 之间的关系, 我们在处理前就进行这样的递归调用,
//            // 可以保证我们一开始就是针对最底层的 ResultMap 进行一对多映射处理
//            // 这样的思想取决于图论算法中的深度优先遍历
//            List res = null;
//
//            // 获取当前的 ResultMap 中的 子ResultMap
//            /**
//             * step1: childResultMap = {
//             *     'studentList': [ResultMap@12(id='student', resultType=Student.class)]
//             * }
//             *
//             * step2: childResultMap = {
//             *
//             * }
//             */
//            Map<String, ResultMap> childResultMap = resultMap.getChildResultMap();
//            for (String f : childResultMap.keySet()) {
//                /**
//                 * step1: 拿到当前 Course-ResultMap 下 的 Student-ResultMap
//                 *
//                 */
//                ResultMap map = childResultMap.get(f);
//                List childList = new ArrayList();
//                // 循环处理每一行数据
//                for (Object t : list) {
//                    try {
//                        /**
//                         *      数据取出关系前 -> 取出的数据
//                         *      Course@1(1, '马老师', [Student@1(1, '马老师的技霸')])    ->   Student@1(1, '马老师的技霸')
//                         *      Course@2(1, '马老师', [Student@2(2, '马老师的高玩')])    ->   Student@2(2, '马老师的高玩')
//                         *      Course@3(1, '马老师', [Student@3(3, '皮燕子')])   ->   Student@3(3, '皮燕子')
//                         *      Course@4(1, '马老师', [Student@4(4, '班花胡莉娅')])     ->   Student@4(4, '班花胡莉娅')
//                         *
//                         *      然后将取出来的数据存入到上面的 childList
//                         */
//                        Object child;
//                        Field declaredField;
//                        declaredField = t.getClass().getDeclaredField(f);
//                        declaredField.setAccessible(true);
//                        child = declaredField.get(t);
//
//                        if (child instanceof List) {
//                            childList.add(((List) child).get(0));
//                        } else {
//                            childList.add(child);
//                        }
//
//                    } catch (NoSuchFieldException e) {
//                        e.printStackTrace();
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                /**
//                 * 到了这一步, childList 数据如下:
//                 * childList => [
//                 *      Student@1(1, '马老师的技霸'),
//                 *      Student@2(2, '马老师的高玩'),
//                 *      Student@3(3, '皮燕子'),
//                 *      Student@4(4, '班花胡莉娅')
//                 * ]
//                 *
//                 * map => {
//                 *                  id: "Student",
//                 *                  resultType: Student.class,
//                 *                  resultFieldList: [
//                 *                      {
//                 *                          column: 'student_id',
//                 *                          columnClassType: Integer.class,
//                 *                          prototype: 'studentId',
//                 *                          primaryKey: false,
//                 *                          oneMap: null,
//                 *                          listMap: null
//                 *                      },
//                 *                      {
//                 *                          column: 'student_name',
//                 *                          columnClassType: String.class,
//                 *                          prototype: 'studentName',
//                 *                          primaryKey: false,
//                 *                          oneMap: null,
//                 *                          listMap: null
//                 *                      }
//                 *                  ]
//                 *            }
//                 *
//                 *  alreadyReadMapList = [Course.class]
//                 */
//                res = oneToMany(childList, map, alreadyReadMapList);
//
//
//                /**
//                 * step1: res = [
//                 *          Student@1(1, '马老师的技霸'),
//                 *          Student@2(2, '马老师的高玩'),
//                 *          Student@3(3, '皮燕子'),
//                 *          Student@4(4, '班花胡莉娅')
//                 * ]
//                 */
//                // 判断对应关系, resultMap 和 map 之间的对应关系是 一对一 还是 一对多
//                boolean one = false;
//                for (ResultMap.ResultField resultField : resultMap.getResultFieldList()) {
//                    if (resultField.getPrototype().equals(f)) {
//                        one = resultField.getOneMap() != null;
//                        break;
//                    }
//                }
//
//                // 然后再将
//                // 如果是一对一, 那么我们就将 Teacher 和 Course 的值合并在一起,作为一个key,
//                if (one) {
//                    List<String> fieldList = map.fieldId();
//
//                    // 这个 objectMap 用来存放 一对一 映射中的子方的.
//                    Map<String, Object> objectMap = new HashMap();
//
//                    // 这一步的打算就是计算出每个 Course 的值, 以及对应的 Course 对象
//                    for (Object child : res) {
//                        String key = values(child, fieldList);
//                        if (!objectMap.containsKey(key)) {
//                            objectMap.put(key, child);
//                        }
//                    }
//
//
//                    // 让 Teacher 中的 Course 字段 指向 同一个 Object 引用
//                    for (Object teacher : list) {
//                        for (Field declaredField : teacher.getClass().getDeclaredFields()) {
//                            if (declaredField.getName().equals(f)) {
//                                declaredField.setAccessible(true);
//                                try {
//                                    Object course = declaredField.get(teacher);
//                                    String key = values(course, fieldList);
//                                    declaredField.set(teacher, objectMap.get(key));
//                                } catch (IllegalAccessException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    }
//
//                    // 此时, Teacher 下的 Course 引用已经解决了. 现在要解决的就是不同的 Teacher
//                    Map<String, Object> teacherMap = new HashMap();
//                    List<String> fList = resultMap.fieldId();
//                    for (Object teacher : list) {
//                        String key = values(teacher, fList);
//                        if (!teacherMap.containsKey(key)) {
//                            teacherMap.put(key, teacher);
//                        }
//                    }
//
//                    // 最后一步, 将 Teacher 的值进行一个整合, 然后返回
//                    return teacherMap.values().stream().map(bean -> (T) bean).collect(Collectors.toList());
//                }
//
//                // 如果是一对多, 我们要做的事情就是首先还是要过滤一遍Teacher,
//                else {
//                    // 定义一个存放一方数据的Map, key就是这个一方的数据的唯一表示
//                    Map<String, Object> teacherMap = new HashMap();
//                    // 定义一个存放多方数据的Map, key就是这个一方数据的唯一表示
//                    Map<String, List> tMap = new HashMap();
//                    // 获取当前resultMap中的id字段,如果没用定义id,则取不涉及映射的所有字段组成作为一个id大字段
//                    List<String> fList = resultMap.fieldId();
//
//                    // 循环遍历一方的数据,因为多方的数据其实就是一方的数据的子数据的合集, 所以这里一方的数据和多方的数据长度
//                    // 是一致的, 以至于我们可以通过同样的 i 索引来获取
//                    for (int i = 0; i < list.size(); i++) {
//                        Object teacher = list.get(i);
//                        Object course = res.get(i);
//                        // 基于一方的数据和一方的id字段得到一个key, 这个key的内容如下: "id=1&name=张三&"
//                        String key = values(teacher, fList);
//
//                        // 如果在这个一方数据的Map中,不存在同样的key了, 我们在添加进去
//                        if (!teacherMap.containsKey(key)) {
//                            teacherMap.put(key, teacher);
//                        }
//                        // 从存放多方数据的Map中取出这个key对应的List
//                        List courseList = tMap.get(key);
//                        // 如果不存在,则创建并添加进去
//                        if (courseList == null) {
//                            courseList = Lists.newArrayList();
//                            tMap.put(key, courseList);
//                        }
//                        // 这个list再添加多方的数据
//                        courseList.add(course);
//                    }
//
//                    /**
//                     * 上面的步骤执行完毕后:
//                     *
//                     * step1:
//                     *      teacherMap => {
//                     *          "courseId=1&courseName=马老师" : Course@1(1, '马老师', [Student@1(1, '马老师的技霸')])
//                     *      }
//                     *
//                     *      tMap => {
//                     *          "courseId=1&courseName=马老师": [
//                     *              Student@1(1, '马老师的技霸'),
//                     *              Student@2(2, '马老师的高玩'),
//                     *              Student@3(3, '皮燕子'),
//                     *              Student@4(4, '班花胡莉娅')
//                     *          ]
//                     *      }
//                     *
//                     *
//                     */
//
//                    // 将 tMap 和 teacherMap 通过相同的 key 进行合并
//                    for (String primary : teacherMap.keySet()) {
//                        Object teacher = teacherMap.get(primary);
//                        List courseList = tMap.get(primary);
//                        try {
//                            // 修改一方中对应的多方的引用
//                            Field field = teacher.getClass().getDeclaredField(f);
//                            field.setAccessible(true);
//                            field.set(teacher, courseList);
//                        } catch (NoSuchFieldException e) {
//                            e.printStackTrace();
//                        } catch (IllegalAccessException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    /**
//                     * 上面的步骤执行完毕后:
//                     *
//                     * step1:
//                     *      teacherMap => {
//                     *          "courseId=1&courseName=马老师" : Course@1(1, '马老师', [
//                     *                  Student@1(1, '马老师的技霸'),
//                     *                  Student@2(2, '马老师的高玩'),
//                     *                  Student@3(3, '皮燕子'),
//                     *                  Student@4(4, '班花胡莉娅')
//                     *          ])
//                     *      }
//                     *
//                     *      return
//                     *          Course@1(1, '马老师', [
//                     *                  Student@1(1, '马老师的技霸'),
//                     *                  Student@2(2, '马老师的高玩'),
//                     *                  Student@3(3, '皮燕子'),
//                     *                  Student@4(4, '班花胡莉娅')
//                     *          ])
//                     *
//                     *      此时, oneToMany程序执行完毕
//                     */
//
//                    return teacherMap.values().stream().map(bean -> (T) bean).collect(Collectors.toList());
//                }
//            }
//
//            /**
//             * step2:
//             *      return [
//             *          Student@1(1, '马老师的技霸'),
//             *          Student@2(2, '马老师的高玩'),
//             *          Student@3(3, '皮燕子'),
//             *          Student@4(4, '班花胡莉娅')
//             *      ]
//             */
//            return list;
//        }
//
//        /**
//         * 根据每个bean中对应的字段得到一个唯一的值
//         *
//         * @param bean      实例对象
//         * @param fieldList 字段集合
//         * @return
//         */
//        private static String values(Object bean, List<String> fieldList) {
//            StringBuilder sb = new StringBuilder();
//            for (String field : fieldList) {
//                try {
//                    Field f = bean.getClass().getDeclaredField(field);
//                    f.setAccessible(true);
//                    sb.append(f.getName()).append("=");
//                    Object v = f.get(bean);
//                    if (v == null) {
//                        sb.append("null");
//                    } else {
//                        sb.append(v.toString());
//                    }
//                    sb.append("&");
//                } catch (NoSuchFieldException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }
//            return sb.toString();
//        }
//
//        public <T> List<T> selectList(String sql, Class<T> returnClass, Object... args) throws Exception {
//            return selectList(sql, ResultRowHandler.DEFAULT_HANDLER, returnClass, args);
//        }
//
//        public <T> T selectOne(String sql, ResultRowHandler resultRowHandler, Class<T> returnClass, Object... args) throws Exception {
//            List<T> res = selectList(sql, resultRowHandler, returnClass, args);
//            if (res.size() > 1) {
//                throw new RuntimeException("return dataSize allow max = 1");
//            }
//            return res.get(0);
//        }
//
//        public <T> T selectOne(String sql, Class<T> returnClass, Object... args) throws Exception {
//            List<T> res = selectList(sql, returnClass, args);
//            if (res.size() > 1) {
//                throw new RuntimeException("return dataSize allow max = 1");
//            }
//            return res.get(0);
//        }
//
//        public boolean execute(String sql, Object... args) {
//            try {
//                boolean res = buildPrepareStatement(sql, args).execute();
//                return res;
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//            return false;
//        }
//
//        public int executeUpdate(String sql, Object... args) {
//            try {
//                int res = buildPrepareStatement(sql, args).executeUpdate();
//                return res;
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//            return 0;
//        }
//
//        public void close() {
//            try {
//                if (connection != null && !connection.isClosed()) {
//                    connection.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//}
