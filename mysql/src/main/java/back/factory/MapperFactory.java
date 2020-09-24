//package back.factory;
//
//import cn.bbzzzs.common.db.DBSource;
//import cn.bbzzzs.common.util.ClassUtils;
//import back.factory.handler.MapperProxy;
//
///**
// * created by TMT
// */
//public class MapperFactory {
//
//    public static <T> T getMapper(Class<T> mapper) {
//        try {
//            return ClassUtils.createProxy(mapper, new MapperProxy(null));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//}
