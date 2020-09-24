//package back.factory.result;
//
//import cn.bbzzzs.common.annotation.Column;
//import cn.bbzzzs.common.util.ReflexUtils;
//import cn.bbzzzs.common.util.StringUtils;
//import lombok.Data;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * created by TMT
// *
// * <resultMap type="com.test" id="testResultMap">
// * <id property="id" column="id" />
// *
// * </resultMap>
// */
//@Data
//public class ResultMap {
//    private Class beanClass;
//    private String id;
//    private List<ProtoType> protoTypeList = new ArrayList();
//    private List<ResultMap> resultMapList = new ArrayList();
//
//    @Data
//    public static class ProtoType {
//        private String property;
//        private String column;
//
//        public ProtoType(String property, String column) {
//            this.property = property;
//            this.column = column;
//        }
//
//        public static ProtoType build(Field field) {
//            String property = field.getName();
//
//            Column column = field.getAnnotation(Column.class);
//            if (column != null) {
//                return new ProtoType(property, column.value());
//            }
//            return new ProtoType(property, StringUtils.reverseHump(property));
//        }
//    }
//
//    public static List<ProtoType> buildProtoTypeList(Class beanClass) {
//        List<Field> fieldList = ReflexUtils.fieldList(beanClass);
//        List<ProtoType> protoTypeList = new ArrayList();
//        fieldList.forEach(field -> protoTypeList.add(ProtoType.build(field)));
//        return protoTypeList;
//    }
//}
