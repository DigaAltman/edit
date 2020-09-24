//package cn.bbzzzs.common.db;
//
//import com.google.common.collect.Lists;
//import lombok.Data;
//import lombok.ToString;
//import lombok.experimental.Accessors;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * @author bbzzzs
// * @description 结果集处理容器
// *
// * 参考 mybatis 中的 mapper.xml 中的 ResultMap
// * <resultMap id = 'cn.bbzzzs.pojo.Student' type = 'cn.bbzzzs.pojo.Student'>
// *      <result column='name' prototype='name'/>
// *      ...
// * </resultMap>
// *
// */
//@Data
//@ToString(exclude = "resultFieldList")
//public class ResultMap {
//
//    public ResultMap(String id, Class resultType) {
//        this.id = id;
//        this.resultType = resultType;
//    }
//
//    /**
//     * resultMap 的 id 标识
//     */
//    private String id;
//
//    /**
//     * resultMap 需要返回的结果集
//     */
//    private Class resultType;
//
//    /**
//     * resultMap 中的实体类对应的字段名称
//     */
//    private List<ResultField> resultFieldList = new LinkedList();
//
//    /**
//     * 获取当前的 ResultMap 中的 id 字段, 如果没有配置, 则取
//     *
//     * @return
//     */
//    public List<String> fieldId() {
//        for (ResultField resultField : resultFieldList) {
//            if (resultField.isPrimaryKey()) {
//                return Lists.newArrayList(resultField.prototype);
//            }
//        }
//
//        List<String> res = Lists.newArrayList();
//        for (ResultField resultField : resultFieldList) {
//            if (resultField.listMap == null && resultField.oneMap == null) {
//                res.add(resultField.prototype);
//            }
//        }
//
//        return res;
//    }
//
//    public Map<String, ResultMap> getChildResultMap() {
//        Map<String,ResultMap> map = new LinkedHashMap();
//        resultFieldList.stream().filter(f -> f.getOneMap() != null || f.getListMap() != null).forEach(f -> {
//            map.put(f.getPrototype(), f.getListMap() == null ? f.getOneMap() : f.getListMap());
//        });
//        return map;
//    }
//
//    /**
//     * 每个类字段和数据库字段之间的映射关系的表现
//     */
//    @Data
//    @Accessors(chain = true)
//    @ToString(exclude = {"oneMap", "listMap"})
//    public class ResultField {
//        /**
//         * sql 字段名称
//         */
//        private String column;
//
//        /**
//         * 实体类字段类型
//         */
//        private Class columnClassType;
//
//        /**
//         * 实体类字段名称
//         */
//        private String prototype;
//
//        /**
//         * 是否为主键, 解决一对多集合映射的关键和一对一集合映射的关键
//         */
//        private boolean primaryKey;
//
//        /**
//         * 这个字段对应 一对一 关联映射
//         */
//        private ResultMap oneMap;
//
//        /**
//         * 这个字段对应 一对多 关联映射
//         */
//        private ResultMap listMap;
//
//    }
//
//    /**
//     * 当前 ResultMap 下的 ResultField 中是否包含 listMap
//     *
//     * @return
//     */
//    public boolean hasListMap() {
//        return this.resultFieldList.parallelStream().anyMatch(f -> f.listMap != null);
//    }
//
//    /**
//     * 当前 ResultMap 下的 ResultField 中是否包含 oneMap
//     *
//     * @return
//     */
//    public boolean hasOneMap() {
//        return this.resultFieldList.parallelStream().anyMatch(f -> f.oneMap != null);
//    }
//
//    /**
//     * 获取当前 ResultMap 涉及到的类信息, 包括 一对多ResultMap 涉及到的类信息 和 一对一 涉及
//     * 到的类信息, 这里可能存在循环引用问题. 所以我们使用 Set<Class> 来解决循环引用
//     *
//     * @return
//     */
//    public Set<Class> getDependentClassSet() {
//        Set<Class> dependentClassSet = new LinkedHashSet(16);
//        getDependentClassSet(dependentClassSet, this);
//        return dependentClassSet;
//    }
//
//    private void getDependentClassSet(Set<Class> dependentClassSet, ResultMap... mapList) {
//        for (int i = 0; i < mapList.length; i++) {
//            ResultMap resultMap = mapList[i];
//
//            // 终止条件
//            if (dependentClassSet.contains(resultMap.getResultType())) {
//                return;
//            }
//
//            dependentClassSet.add(resultMap.getResultType());
//
//            List<ResultMap> resultMapList = resultMap.getResultFieldList().parallelStream().filter(f -> f.getListMap() != null || f.getOneMap() != null).map(f -> f.getOneMap() == null ? f.getListMap() : f.getOneMap()).collect(Collectors.toList());
//
//            getDependentClassSet(dependentClassSet, resultMapList.toArray(new ResultMap[resultMapList.size()]));
//        }
//
//    }
//
//
//    /*===========================================================*/
//    /*================ 重写 equals 和 hashCode 方法 ================*/
//    /*===========================================================*/
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof ResultMap)) return false;
//        ResultMap resultMap = (ResultMap) o;
//        return Objects.equals(getId(), resultMap.getId());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(getId());
//    }
//}
