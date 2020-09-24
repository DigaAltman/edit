package cn.bbzzzs.db.result.bean;

import cn.bbzzzs.common.util.ReflexUtils;
import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.db.annotation.Column;
import cn.bbzzzs.db.annotation.Id;
import cn.bbzzzs.db.result.factory.ResultMapFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Blob;
import java.util.*;

@Data
@Accessors(chain = true)
@ToString(exclude = "resultList")
public class ResultMap implements Serializable {
    /**
     * 返回结果映射容器对应的 id
     */
    private String id;

    /**
     * 返回结果映射容器对应的类全路径名称
     */
    private String type;

    /**
     * 字段对应的子字段
     */
    private List<Result> resultList = new ArrayList();

    /**
     * ORM 映射的基本类型
     */
    public static Set<Class> basicTypeSet = Sets.newHashSet(String.class, Byte.class, Character.class, Boolean.class, Integer.class, Double.class, Float.class, Short.class, Long.class, byte.class, char.class, boolean.class, int.class, double.class, float.class, short.class, long.class, BigDecimal.class, Date.class, Blob.class);

    /**
     * 基于指定类类型生成 ResultMap
     *
     * @param clazz 指定的类类型
     * @return
     */
    public static ResultMap generate(ResultMapFactory factory, Class clazz) {
        // 判断 ORM 类型是否合法
        if (clazz == null || clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            throw new IllegalArgumentException("不支持的 ORM 类型");
        }

        // 从彻底完成的缓存中取出对象
        ResultMap resultMap = factory.getMap(clazz);

        // 如果不存在,直接去初始化
        if (resultMap == null) {
            resultMap = doGenerate(factory, clazz);
        }

        return resultMap;
    }

    /**
     * 尝试解析依赖的类,因为这个 ResultMap 中可能包含其他的 ResultMap, 其他的ResultMap中包含当前正在
     * 解析的 ResultMap， 我们需要解决这种循环引用
     *
     * @param clazz
     * @return
     */
    private static ResultMap doGenerate(ResultMapFactory factory, Class clazz) {
        // 首先从简单初始化后的 ResultMap 缓存中取出对应 id 的 ResultMap
        ResultMap resultMap = factory.getEasyMap(clazz);

        // 如果缓存中存在则直接返回
        if (resultMap != null) {
            return resultMap;
        }

        // 创建 ResultMap, 此时 ResultMap 中的 List<ResultFiled> 还没有内容
        resultMap = new ResultMap();
        resultMap.setId(clazz.getName());
        resultMap.setType(clazz.getName());

        // 开始处理 ResultMap 下的 字段映射
        List<Result> resultList = resultMap.getResultList();

        // 需要处理的所有字段
        List<Field> fieldList = ReflexUtils.fieldList(clazz);

        // 需要处理的特殊字段, 比如 一对一 , 一对多
        List<Field> needSpecialHandleFieldList = new LinkedList();

        // 基本类型字段处理
        for (int i = 0; i < fieldList.size(); i++) {
            // 获取当前字段
            Field field = fieldList.get(i);

            // 当前字段的类型
            Class columnClassType = field.getType();

            // 当前字段名称
            String prototype = field.getName();

            // 转驼峰后的 sql 字段名称
            String column = StringUtils.reverseHump(prototype);

            // 是否为主键
            boolean primaryKey = false;

            // 获取字段上的列注解, 如果用户配置了 @Column 注解,则 数据表字段名称 取 @Column 注解中的 value 值
            Column col = field.getAnnotation(Column.class);
            if (col != null) {
                column = col.value();
            }

            // 获取字段上的 @Id 注解, 如果用户配置了 @Id 注解,则表示这个字段是 ResultMap 中的 id 字段
            Id primary = field.getAnnotation(Id.class);

            if (primary != null) {
                // 因为存在 @Id("course_id") 的情况, 如果 @Id 中配置了列名称,我们就不使用 @Column 中的名称了
                column = primary.value().trim().equals("") ? column : primary.value();
                primaryKey = true;
            }

            // 构建一个 ResultField 然后填充进去就可以了
            Result result = new Result();
            result.setColumn(column).setType(columnClassType).setPrimary(primaryKey).setProperty(prototype);


            // 如果这个字段不属于基本类型, 那就放到 特殊类型字段集合中, 最后统一进行处理
            if (!basicTypeSet.parallelStream().anyMatch(t -> t.isAssignableFrom(columnClassType))) {
                needSpecialHandleFieldList.add(field);
            }

            resultList.add(result);
        }

        // 到了这一步, 简单类型的字段映射解析就完成了, 我们就可以将这个 ResultMap 放入到简单缓存中了
        // 下次, 在进行循环引用触发的递归调用事件时, 就可以依靠这个 easyResultMapCache 结束递归了
        factory.putEasyMap(clazz, resultMap);

        // 特殊类型字段处理
        for (int i = 0; i < needSpecialHandleFieldList.size(); i++) {
            Field field = needSpecialHandleFieldList.get(i);
            Class columnClassType = field.getType();
            ResultMap associationMap = null;
            ResultMap collectionMap = null;

            // 不是基本类型, 那就要考虑是一对多还是一对一的问题了, 如果它是 集合 类型,那么我们就需要获取它的泛型
            if (Collection.class.isAssignableFrom(columnClassType)) {
                // 获取泛型
                Type genericType = field.getGenericType();
                // 如果没有声明泛型， 则作为 Map 处理
                if (null == genericType) {
                    return factory.getEasyMap(Map.class);
                }

                if (genericType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) genericType;
                    // 得到泛型里的 class 类型对象
                    Class<?> finalClass = (Class<?>) pt.getActualTypeArguments()[0];

                    // 然后根据这个 类型 去获取对应的 ResultMap， 它此时就会去调用 build
                    collectionMap = generate(factory, finalClass);
                }
            } else {
                // 如果是一对一, 那就直接根据字段类型去获取对应的 ResultMap 就可以了
                associationMap = generate(factory, columnClassType);
            }

            // 找到对应的 Result 然后填充依赖进去就可以了
            Result result = resultMap.getResultList().stream().filter(f -> f.getProperty().equals(field.getName())).findFirst().get();
            result.setAssociationMap(associationMap).setCollectionMap(collectionMap);
        }

        // 此时, 整个 ResultMap 已经填充完毕了. 我们在将它放入到 彻底完成的缓存中就可以了
        factory.putMap(clazz, resultMap);

        // 删除临时缓存中的 key 为 clazz 的 ResultMap
        factory.removeEasyMap(clazz);

        // 最终返回出去
        return resultMap;
    }


    /**
     * 获取当前ResultMap下的所有的ResultMap， 包括 子ResultMap 和 孙ResultMap ...
     * 包括当前ResultMap
     *
     * @return
     */
    public Set<ResultMap> dependencies() {
        Set<ResultMap> resultMapSet = new LinkedHashSet();

        getDependencies(this, resultMapSet);
        return resultMapSet;
    }

    /**
     * 具体获取 ResultMap 下的所有 ResultMap 的核心方法
     *
     * @param resultMap
     * @param resultMapSet
     */
    private void getDependencies(ResultMap resultMap, Set<ResultMap> resultMapSet) {
        // 这里不能直接使用 resultMapSet.contains(resultMap) ,因为Set的contains方法,会出现 StackException
        if (!resultMapSet.contains(resultMap)) {
            resultMapSet.add(resultMap);
            resultMap.childMapResult().stream()
                    .filter(f -> f.getCollectionMap() != null || f.getAssociationMap() != null)
                    .forEach(f -> {
                        ResultMap map = f.getAssociationMap();
                        if (map == null) {
                            map = f.getCollectionMap();
                        }

                        getDependencies(map, resultMapSet);
                    });
        }
    }


    /**
     * 获取当前的 ResultMap 下的 子 Result， 包括 collection， association
     *
     * @return
     */
    public List<Result> childMapResult() {
        LinkedList<Result> resultList = Lists.newLinkedList();
        for (Result result : this.getResultList()) {
            if (result.getAssociationMap() != null || result.getCollectionMap() != null) {
                resultList.add(result);
            }
        }

        return resultList;
    }

    /**
     * 简单填充
     *
     * @param map
     * @return
     */
    public Object mapToBean(Map<String, Object> map) {
        Object instance = null;
        try {
            // 获取 resultMap 的返回值类型
            Class resultType = Class.forName(this.type);

            // 简单初始化
            instance = resultType.newInstance();

            boolean setStatus = false;

            for (int i = 0; i < this.getResultList().size(); i++) {
                // 基于 resultMap 中的映射关系,进行循环赋值操作
                Result result = this.getResultList().get(i);

                // 不会处理 一对一 和 一对多， 只处理简单类型
                if (result.getCollectionMap() == null && result.getAssociationMap() == null) {
                    Object val = map.get(result.getColumn());
                    if (val != null) {
                        ReflexUtils.set(instance, result.getProperty(), val);
                        setStatus = true;
                    }
                }
            }

            // 当没有字段进行赋值时
            if (setStatus == false) {
                return null;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("类实例化失败,错误原因:" + e.getMessage());
        }

        return instance;
    }


    /**
     * 也是一个很重要的方法, 这个方法的作用是进行真正的依赖填充
     * 它会解决 ORM 中的 一对一 循环依赖
     *
     * @param beforeClassMap 解决一对一循环依赖前的对象, 这里所有的 Object 都是只完成了简单字段的值映射.
     *                       还没有完成引用类型字段的值映射
     * @return
     */
    public Object fillBean(Map<ResultMap, Object> beforeClassMap) {
        // 完成 ORM 的 一对一 映射后的集合
        Map<ResultMap, Object> successBeanMap = new HashMap();

        // 循环处理这些还没有解决一对一循环依赖的对象
        beforeClassMap.forEach((k, v) -> {
            // 取出这个没有完成 一对一 循环依赖字段设置的简单对象
            Object bean = v;

            // 根据它的 ResultMap 中的 List<ResultField> 来进行处理. 这里我们把 一对多 映射 也在这里做了一个简单的处理
            k.getResultList().stream().filter(f -> f.getAssociationMap() != null || f.getCollectionMap() != null).forEach(f -> {

                // 拿到一对一映射
                ResultMap map = f.getAssociationMap();

                // 如果不是一对一,那么就是一对多映射
                if (map == null) {
                    map = f.getCollectionMap();
                }
                // 尝试从 完成缓存中 拿到这个 ResultMap 对应的结果
                Object res = successBeanMap.get(map);

                // 如果拿不到,就去 简单缓存中 拿到这个 ResultMap 对应的结果
                if (res == null) {
                    res = beforeClassMap.get(map);
                }

                // 如果这个字段它是 一对多 引用的话
                if (f.getCollectionMap() != null) {
                    if (res != null) {
                        // 那我们就在赋值的时候,以 List 的形式扔进去
                        ReflexUtils.set(bean, f.getProperty(), Arrays.asList(res));
                    } else {
                        // 如果没有查询出多方有内存, 那就赋值一个空 List
                        ReflexUtils.set(bean, f.getProperty(), Arrays.asList());
                    }
                } else {
                    if(bean != null) {
                        // 如果是一对一,那就直接扔进去
                        ReflexUtils.set(bean, f.getProperty(), res);
                    }
                }

            });

            // 最后将这个完成映射处理后的 bean 扔到 successBeanMap 缓存中.
            // 循环套娃解决的思路就是手动更改指针引用
            successBeanMap.put(k, bean);
        });

        // 这里我们就可以清空这个简单缓存了
        beforeClassMap.clear();

        return successBeanMap.get(this);
    }


    /**
     * 获取当前 ResultMap 下的所有 Result 字段, 获取它的 id 标签, 如果没有配置 id 标签, 则将所有的
     * result 标签组合成一个 临时id 标签
     *
     * @return
     */
    public List<String> fieldId() {
        for (Result result : resultList) {
            if (result.isPrimary) {
                return Lists.newArrayList(result.property);
            }
        }

        List<String> res = Lists.newArrayList();
        for (Result result : resultList) {
            if (result.getCollectionMap() == null && result.getAssociationMap() == null) {
                res.add(result.property);
            }
        }

        return res;
    }


    /*===========================================================*/
    /*================ 重写 equals 和 hashCode 方法 ================*/
    /*===========================================================*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResultMap)) return false;
        ResultMap resultMap = (ResultMap) o;
        return Objects.equals(getId(), resultMap.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}



