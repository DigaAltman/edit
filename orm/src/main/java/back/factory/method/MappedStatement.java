//package back.factory.method;
//
//import cn.bbzzzs.common.util.ClassUtils;
//import cn.bbzzzs.common.util.ReflexUtils;
//import back.annotation.*;
//import back.factory.result.ResultMap;
//import back.mapper.DBTableMapper;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Method;
//import java.lang.reflect.Parameter;
//import java.lang.reflect.Type;
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * created by TMT
// */
//@Getter
//public class MappedStatement {
//    /**
//     * sql 主体
//     */
//    @Setter
//    private String sql;
//
//    private String id;
//
//    private ResultMap resultMap;
//
//    private SqlType sqlType;
//
//    private List<String> paramNameList;
//
//    /**
//     * 尝试解析方法, 如果方法不符合规范. 直接抛出异常
//     *
//     * @param method
//     * @return
//     */
//    public static boolean tryBuild(Method method) {
//        Annotation[] annotations = method.getAnnotations();
//        return Arrays.stream(annotations).anyMatch(annotation -> {
//            Class<? extends Annotation> annotationType = annotation.annotationType();
//            return annotationType == Select.class || annotationType == Update.class || annotationType == Delete.class || annotationType == Insert.class || resolveMethod(method);
//        });
//    }
//
//    public static MappedStatement build(Method method) {
//        MappedStatement mappedStatement = new MappedStatement();
//
//        mappedStatement.id = method.getDeclaringClass().getName() + "." + method.getName() + Arrays.stream(method.getParameters()).map(p -> p.getName()).collect(Collectors.toList()).toString();
//
//        List<Annotation> annotationList = Arrays.asList(method.getAnnotations());
//        for (Annotation annotation : annotationList) {
//            if (annotation.annotationType() == Select.class) {
//                Select select = (Select) annotation;
//                mappedStatement.sql = select.value();
//                mappedStatement.sqlType = SqlType.SELECT;
//
//                ResultMap resultMap = new ResultMap();
//                Class returnType = method.getReturnType();
//
//                if (List.class.isAssignableFrom(returnType)) {
//                    List<Type> typeList = ReflexUtils.getMethodReturnTypes(method);
//                    resultMap.setBeanClass(ClassUtils.forName(typeList.get(0).getTypeName()));
//                }
//                // todo resultMap 解析
//                else {
//                    resultMap.setBeanClass(ClassUtils.forName(returnType.getTypeName()));
//                }
//                resultMap.setId(System.currentTimeMillis() + "");
//
//                Result result = method.getAnnotation(Result.class);
//                if (result == null) {
//                    List<ResultMap.ProtoType> protoTypeList = ResultMap.buildProtoTypeList(resultMap.getBeanClass());
//                    resultMap.setProtoTypeList(protoTypeList);
//                    mappedStatement.resultMap = resultMap;
//                }
//                // todo result存在如何处理
//            }
//            if (annotation.annotationType() == Update.class) {
//                Update select = (Update) annotation;
//                mappedStatement.sql = select.value();
//                mappedStatement.resultMap = null;
//                mappedStatement.sqlType = SqlType.UPDATE;
//            }
//            if (annotation.annotationType() == Delete.class) {
//                Delete select = (Delete) annotation;
//                mappedStatement.sql = select.value();
//                mappedStatement.resultMap = null;
//                mappedStatement.sqlType = SqlType.DELETE;
//            }
//            if (annotation.annotationType() == Insert.class) {
//                Insert select = (Insert) annotation;
//                mappedStatement.sql = select.value();
//                mappedStatement.resultMap = null;
//                mappedStatement.sqlType = SqlType.INSERT;
//            }
//        }
//
//        mappedStatement.paramNameList = new ArrayList();
//
//        for (Parameter parameter : method.getParameters()) {
//            Param param = parameter.getAnnotation(Param.class);
//            if (param != null) {
//                mappedStatement.paramNameList.add(param.value());
//            } else {
//                mappedStatement.paramNameList.add(param.value());
//            }
//        }
//
//        return mappedStatement;
//    }
//
//
//    // todo 尝试解析方法命名规则
//    public static boolean resolveMethod(Method method) {
//        return true;
//    }
//
//    public static void main(String[] args) throws Exception {
//        build(DBTableMapper.class.getMethod("selectTableList", String.class));
//    }
//}
