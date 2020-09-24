//package cn.bbzzzs.common.db;
//
//import cn.bbzzzs.common.annotation.Column;
//import cn.bbzzzs.common.annotation.Id;
//import cn.bbzzzs.common.util.StringUtils;
//import com.google.common.collect.Sets;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.Modifier;
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.math.BigDecimal;
//import java.sql.Blob;
//import java.util.*;
//
///**
// * @author bbzzzs
// * @description 结果集处理容器构建类
// */
//public class ResultMapBuilder {
//    /**
//     * 缓存彻底初始化的 ResultMap, 用户自定义的 ResultMap 解析后的结果也应该要放在这里
//     */
//    private static Map<String, ResultMap> resultMapCache = new LinkedHashMap(8);
//
//    /**
//     * 缓存已经简单初始化的 ResultMap， 里面关于引用其他 ResultMap 的字段引用一直是 null
//     */
//    private static Map<String, ResultMap> easyResultMapCache = new LinkedHashMap(8);
//
//    /**
//     * ORM 映射的基本类型
//     */
//    public static Set<Class> basicTypeSet = Sets.newHashSet(String.class, Byte.class, Character.class, Boolean.class, Integer.class, Double.class, Float.class, Short.class, Long.class, byte.class, char.class, boolean.class, int.class, double.class, float.class, short.class, long.class, BigDecimal.class, Date.class, Blob.class);
//
//    /**
//     * 保存 ResultMap
//     */
//    public static synchronized void save(String id, ResultMap resultMap) {
//        resultMapCache.put(id, resultMap);
//    }
//
//
//    /**
//     * 基于返回类型构建 ResultMap,这里也存在 ResultMap 相互依赖的问题， 所以需要两个 ResultMap 缓存容器
//     * <p>
//     * 比如:
//     * <p>
//     * class Student {
//     * private Integer id;
//     * private String name;
//     * private Course course;
//     * }
//     * <p>
//     * class Course {
//     * private Integer id;
//     * private Student student;
//     * }
//     * <p>
//     * 当我们将 Student 解析为一个 ResultMap 时, 发现它存在一个 Course 类型的字段,
//     * 于是我们有需要将 Course 也解析为一个 ResultMap， 然后 Course 也存在一个 Student,
//     * 于是我们又去 解析 Student... 反复套娃,最终 StackException
//     *
//     * @param resultType 返回类类型
//     * @return 返回一个构建好的 ResultMap
//     */
//    public static ResultMap build(Class resultType) {
//        // 如果返回的类型是 null 或者 是 接口 或者 是基本类型, 我们就没必要去解析了
//        if (resultType == null || resultType.isInterface() || Modifier.isAbstract(resultType.getModifiers())) {
//            throw new IllegalArgumentException("不支持的返回类型");
//        }
//
//        // 获取 resultType 对应的 id
//        String id = resultType.getName();
//
//        // 首先, 常用从彻底初始化后的 ResultMap 缓存中取出对应类型的
//        ResultMap resultMap = resultMapCache.get(id);
//
//        // 如果缓存中不存在, 我们再去初始化这个 ResultMap
//        if (resultMap == null) {
//            return doBuild(id, resultType);
//        }
//        return resultMap;
//    }
//
//    private static ResultMap doBuild(String id, Class resultType) {
//        // 首先从简单初始化后的 ResultMap 缓存中取出对应 id 的 ResultMap
//        ResultMap resultMap = easyResultMapCache.get(id);
//
//        // 如果缓存中存在则直接返回
//        if (resultMap != null) {
//            return resultMap;
//        }
//
//        // 创建 ResultMap, 此时 ResultMap 中的 List<ResultFiled> 还没有内容
//        resultMap = new ResultMap(id, resultType);
//
//        // 开始处理 ResultMap 下的 字段映射
//        List<ResultMap.ResultField> resultFieldList = resultMap.getResultFieldList();
//
//        // 需要处理的所有字段
//        Field[] fieldList = resultType.getDeclaredFields();
//
//        // 需要处理的特殊字段, 比如 一对一 , 一对多
//        List<Field> needSpecialHandleFieldList = new LinkedList();
//
//        // 基本类型字段处理
//        for (int i = 0; i < fieldList.length; i++) {
//            // 获取当前字段
//            Field field = fieldList[i];
//
//            // 当前字段的类型
//            Class columnClassType = field.getType();
//
//            // 当前字段名称
//            String prototype = field.getName();
//
//            // 转驼峰后的 sql 字段名称
//            String column = StringUtils.reverseHump(prototype);
//
//            // 是否为主键
//            boolean primaryKey = false;
//
//            // 获取字段上的列注解, 如果用户配置了 @Column 注解,则 数据表字段名称 取 @Column 注解中的 value 值
//            Column col = field.getAnnotation(Column.class);
//            if (col != null) {
//                column = col.value();
//            }
//
//            // 获取字段上的 @Id 注解, 如果用户配置了 @Id 注解,则表示这个字段是 ResultMap 中的 id 字段
//            Id primary = field.getAnnotation(Id.class);
//
//            if (primary != null) {
//                // 因为存在 @Id("course_id") 的情况, 如果 @Id 中配置了列名称,我们就不使用 @Column 中的名称了
//                column = primary.value().trim().equals("") ? column : primary.value();
//                primaryKey = true;
//            }
//
//            // 构建一个 ResultField 然后填充进去就可以了
//            ResultMap.ResultField resultField = resultMap.new ResultField();
//            resultField = resultField.setColumn(column).setColumnClassType(columnClassType).setPrimaryKey(primaryKey).setPrototype(prototype);
//
//
//            // 如果这个字段不属于基本类型, 那就放到 特殊类型字段集合中, 最后统一进行处理
//            if (!basicTypeSet.parallelStream().anyMatch(t -> t.isAssignableFrom(columnClassType))) {
//                needSpecialHandleFieldList.add(field);
//            }
//
//            resultFieldList.add(resultField);
//        }
//
//        // 到了这一步, 简单类型的字段映射解析就完成了, 我们就可以将这个 ResultMap 放入到简单缓存中了
//        // 下次, 在进行循环引用触发的递归调用事件时, 就可以依靠这个 easyResultMapCache 结束递归了
//        easyResultMapCache.put(id, resultMap);
//
//        // 特殊类型字段处理
//        for (int i = 0; i < needSpecialHandleFieldList.size(); i++) {
//            Field field = needSpecialHandleFieldList.get(i);
//            Class columnClassType = field.getType();
//            ResultMap listMap = null;
//            ResultMap oneMap = null;
//
//            // 不是基本类型, 那就要考虑是一对多还是一对一的问题了, 如果它是 List 类型,那么我们就需要获取它的泛型
//            if (List.class.isAssignableFrom(columnClassType)) {
//                Type genericType = field.getGenericType();
//
//                if (null == genericType) {
//                    continue;
//                }
//
//                if (genericType instanceof ParameterizedType) {
//                    ParameterizedType pt = (ParameterizedType) genericType;
//                    // 得到泛型里的 class 类型对象
//                    Class<?> finalClass = (Class<?>) pt.getActualTypeArguments()[0];
//
//                    // 然后根据这个 类型 去获取对应的 ResultMap， 它此时就会去调用 build
//                    listMap = build(finalClass);
//                }
//            } else {
//
//                // 如果是一对一, 那就直接根据字段类型去获取对应的 ResultMap 就可以了
//                oneMap = build(columnClassType);
//            }
//
//            // 找到 ResultField 然后填充进去就可以了
//            ResultMap.ResultField resultField = resultMap.getResultFieldList().stream().filter(f -> f.getPrototype().equals(field.getName())).findFirst().get();
//            resultField.setListMap(listMap).setOneMap(oneMap);
//        }
//
//        // 此时, 整个 ResultMap 已经填充完毕了. 我们在将它放入到 彻底完成的缓存中就可以了
//        resultMapCache.put(id, resultMap);
//
//        // 因为已经完成了, 也就不需要还没彻底完成的 ResultMap, 用完就丢. >_<
//        easyResultMapCache.remove(id);
//
//        // 最终返回出去
//        return resultMap;
//    }
//
//    /**
//     * 也是一个很重要的方法, 这个方法的作用是进行真正的依赖填充
//     * 它会解决 ORM 中的 一对一 循环依赖
//     *
//     *
//     * @param resultMap         当前处理返回结果集
//     *
//     * @param beforeClassMap    解决一对一循环依赖前的对象, 这里所有的 Object 都是只完成了简单字段的值映射.
//     *                          还没有完成引用类型字段的值映射
//     * @return
//     */
//    public static Object fillBean(ResultMap resultMap, Map<ResultMap, Object> beforeClassMap) {
//        // 完成 ORM 的 一对一 映射后的集合
//        Map<ResultMap, Object> successBeanMap = new HashMap();
//
//        // 循环处理这些还没有解决一对一循环依赖的对象
//        beforeClassMap.forEach((k, v) -> {
//            // 取出这个没有完成 一对一 循环依赖字段设置的简单对象
//            Object bean = v;
//
//            // 根据它的 ResultMap 中的 List<ResultField> 来进行处理. 这里我们把 一对多 映射 也在这里做了一个简单的处理
//            k.getResultFieldList().stream().filter(f -> f.getListMap() != null || f.getOneMap() != null).forEach(f -> {
//
//                // 拿到一对一映射
//                ResultMap map = f.getOneMap();
//                // 如果不是一对一,那么就是一对多映射
//                if (map == null) {
//                    map = f.getListMap();
//                }
//                try {
//                    // 拿到这个字段
//                    Field field = bean.getClass().getDeclaredField(f.getPrototype());
//                    field.setAccessible(true);
//
//                    // 尝试从 完成缓存中 拿到这个 ResultMap 对应的结果
//                    Object res = successBeanMap.get(map);
//                    // 如果拿不到,就去 简单缓存中 拿到这个 ResultMap 对应的结果
//                    if (res == null) {
//                        res = beforeClassMap.get(map);
//                    }
//                    // 如果这个字段它是 一对多 引用的话
//                    if (List.class.isAssignableFrom(field.getType())) {
//                        // 那我们就在赋值的时候,以 List 的形式扔进去
//                        // List<Student> studentList;
//                        field.set(bean, Arrays.asList(res));
//
//                    } else {
//                        // 如果是一对一,那就直接扔进去
//                        // Course course;
//                        field.set(bean, res);
//                    }
//                } catch (NoSuchFieldException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            });
//
//            // 最后将这个完成映射处理后的 bean 扔到 successBeanMap 缓存中.
//            // 循环套娃解决的思路就是手动更改指针引用
//            successBeanMap.put(k, bean);
//        });
//
//        return successBeanMap.get(resultMap);
//    }
//
//    public static Set<ResultMap> getDependResultMap(ResultMap resultMap) {
//        Set<ResultMap> resultMapSet = new LinkedHashSet();
//        getNeedClassSetByResultMap(resultMap, resultMapSet);
//        return resultMapSet;
//    }
//
//    private static void getNeedClassSetByResultMap(ResultMap resultMap, Set<ResultMap> resultMapSet) {
//        // 这里不能直接使用 resultMapSet.contains(resultMap) ,因为Set的contains方法,会出现 StackException
//        if (!resultMapSet.contains(resultMap)) {
//            resultMapSet.add(resultMap);
//            resultMap.getResultFieldList().stream()
//                    .filter(f -> f.getListMap() != null || f.getOneMap() != null)
//                    .forEach(f -> {
//                        ResultMap map = f.getOneMap();
//                        if (map == null) {
//                            map = f.getListMap();
//                        }
//                        getNeedClassSetByResultMap(map, resultMapSet);
//                    });
//        }
//    }
//}
