//package cn.bbzzzs.common.db;
//
//import cn.bbzzzs.common.util.ReflexUtils;
//
//import java.util.*;
//
///**
// * 这个接口会将数据库返回的每一行的结果都以 LinkedHashMap 的形式扔进来进行处理
// *
// * @author tomcatbbzzzs
// * @description 这个接口是用来解决 java.sql.LinkedHashMap 和 JavaBean 之间的具体转换的
// */
//@FunctionalInterface
//public interface ResultRowHandler {
//
//    /**
//     * 默认处理器
//     */
//    ResultRowHandler DEFAULT_HANDLER = new ResultRowHandler() {
//        @Override
//        public <T> T handle(LinkedHashMap map, Class<T> clazz) {
//            if (Map.class.isAssignableFrom(clazz)) {
//                return (T) map;
//            }
//
//            if (Set.class.isAssignableFrom(clazz)) {
//                Set set = new LinkedHashSet();
//                map.forEach((k, v) -> set.add(v));
//                return (T) set;
//            }
//
//            if (Object[].class.isAssignableFrom(clazz)) {
//                Collection values = map.values();
//                return (T) values.toArray(new Object[values.size()]);
//            }
//
//            if (List.class.isAssignableFrom(clazz)) {
//                List list = new LinkedList();
//                map.forEach((k, v) -> list.add(v));
//                return (T) list;
//            }
//
//            if (ResultMapBuilder.basicTypeSet.contains(clazz)) {
//                return (T) map.values().toArray()[0];
//            } else {
//                // 1. 获取当前类型的返回处理器
//                ResultMap resultMap = ResultMapBuilder.build(clazz);
//
//                // 2. 获取当前需要依赖的类
//                Set<ResultMap> needResultMapSet = ResultMapBuilder.getDependResultMap(resultMap);
//
//                // 3. 进行类的简单值填充
//                Map<ResultMap, Object> beforeClassMap = new LinkedHashMap();
//                needResultMapSet.forEach(result -> beforeClassMap.put(result, ReflexUtils.mapToResultMap(map, result)));
//
//                // 4. 进行真正的依赖填充
//                Object bean = ResultMapBuilder.fillBean(resultMap, beforeClassMap);
//                return (T) bean;
//            }
//        }
//    };
//
//
//    <T> T handle(LinkedHashMap map, Class<T> returnClass);
//}
