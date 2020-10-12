//package back.factory.handler;
//
//import cn.bbzzzs.common.db.DBSource;
//import cn.bbzzzs.common.util.DBUtils;
//import back.factory.method.MappedStatement;
//import back.factory.method.SqlType;
//
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.Method;
//import java.sql.Connection;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * created by TMT
// */
//public class MapperProxy implements InvocationHandler {
//    private Connection connection;
//
//    public MapperProxy(Connection connection) {
//        this.connection = connection;
//    }
//
//    @Override
//    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        DBUtils.DB db = DBUtils.build(connection);
//        if (!MappedStatement.tryBuild(method)) {
//            throw new IllegalArgumentException("方法无法代理");
//        }
//        MappedStatement mappedStatement = MappedStatement.build(method);
//        SqlType sqlType = mappedStatement.getSqlType();
//        switch (sqlType) {
//            case SELECT:
//                Class returnType = method.getReturnType();
//                Class returnClass = mappedStatement.getResultMap().getBeanClass();
//                // 做一个参数处理
//                List<Object> argList = handleSql(mappedStatement, args);
//                if (List.class.isAssignableFrom(returnType)) {
//                    return db.selectList(mappedStatement.getSql(), returnClass, argList.toArray());
//                } else {
//                    return db.selectOne(mappedStatement.getSql(), returnClass, argList.toArray());
//                }
//            case DELETE:
//            case INSERT:
//            case UPDATE:
//                return db.executeUpdate(mappedStatement.getSql(), args);
//        }
//        return null;
//    }
//
//    private static <T> int inArray(List<T> array, T item) {
//        for (int i = 0; i < array.size(); i++) {
//            if (array.get(i).equals(item)) {
//                return i;
//            }
//        }
//        return -1;
//    }
//
//
//
//
//
//    private List<Object> handleSql(MappedStatement mappedStatement, Object[] args) {
//        String sql = mappedStatement.getSql();
//        // 替换 ${} 占位符
//        while (sql.contains("${")) {
//            int start = sql.indexOf("${");
//            int end = sql.indexOf("}");
//            if (end == -1) {
//                throw new IllegalArgumentException("sql占位符有问题!!");
//            }
//            String param = sql.substring(start + 2, end);
//
//            sql = new StringBuilder(sql.substring(0, start)).append(args[inArray(mappedStatement.getParamNameList(), param)]).append(sql.substring(end + 1)).toString();
//        }
//
//        List<Object> paramList = new ArrayList();
//
//        // 替换 #{} 占位符
//        while (sql.contains("#{")) {
//            int start = sql.indexOf("#{");
//            int end = sql.indexOf("}");
//            if (end == -1) {
//                throw new IllegalArgumentException("sql占位符有问题!!");
//            }
//            String param = sql.substring(start + 2, end);
//
//            paramList.add(args[inArray(mappedStatement.getParamNameList(), param)]);
//
//            sql = new StringBuilder(sql.substring(0, start)).append("?").append(sql.substring(end + 1)).toString();
//        }
//
//        mappedStatement.setSql(sql);
//
//        return paramList;
//    }
//}
