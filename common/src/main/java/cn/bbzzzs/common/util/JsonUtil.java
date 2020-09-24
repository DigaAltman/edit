package cn.bbzzzs.common.util;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

import java.util.List;

public class JsonUtil {
    private static Gson gson = new Gson();

    // 基本类型
    private static List<Class> BASIC_CLASS_LIST = Lists.newArrayList(
            Integer.class, Long.class, Double.class, Short.class, Float.class, Boolean.class, Byte.class, Character.class, String.class,
            int.class, long.class, double.class, short.class, float.class, boolean.class, byte.class, char.class);


    public static <T> T stringToBean(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }


    public static <T> String beanToString(T bean) {
        return gson.toJson(bean, bean.getClass());
    }

}
