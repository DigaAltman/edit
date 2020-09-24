package cn.bbzzzs.common.util;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ReflexUtils {

    /**
     * 获取当前类下的所有字段
     *
     * @param beanClass
     * @return
     */
    public static List<Field> fieldList(Class beanClass) {
        return Arrays.asList(beanClass.getDeclaredFields());
    }


    /**
     * 通过字段设置值
     *
     * @param bean
     * @param fieldName
     * @param value
     * @param <T>
     */
    public static <T> void set(Object bean, String fieldName, T value) {
        try {
            Field field = bean.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(bean, value);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过字段获取值
     *
     * @param bean
     * @param fieldName
     * @param <T>
     * @return
     */
    public static <T> T get(Object bean, String fieldName) {
        T val = null;
        try {
            Field field = bean.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            val = (T) field.get(bean);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return val;
    }

    /**
     * 调用Setter方法
     *
     * @param bean
     * @param field
     * @param value
     * @param <T>
     */
    public static <T> void invokeSetter(Object bean, Field field, T value) {
        Class fieldType = field.getType();
        Class beanClass = bean.getClass();
        Method method = null;
        try {
            method = beanClass.getMethod(String.format("%s%s", "set", StringUtils.firstUpper(field.getName())), fieldType);
            method.invoke(bean, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 调用 Getter 方法
     *
     * @param bean
     * @param field
     * @param <T>
     * @return
     */
    public static <T> T invokeGetter(Object bean, Field field) {
        Class fieldType = field.getType();
        Class beanClass = bean.getClass();
        Method method = null;
        try {
            method = beanClass.getMethod(String.format("%s%s", "get", StringUtils.firstUpper(field.getName())));
            return (T) method.invoke(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取方法的返回值类型
     *
     * @param method
     * @return
     */
    public static Type getMethodReturnType(Method method) {
        Type type = method.getGenericReturnType();
        return type;
    }

    /**
     * 获取方法返回值中的泛型
     *
     * @param method
     * @return
     */
    public static List<Type> getMethodReturnTypes(Method method) {
        // 获取返回值类型
        Type type = getMethodReturnType(method);
        if (type instanceof ParameterizedType) { // 判断获取的类型是否是参数类型
            Type[] typesto = ((ParameterizedType) type).getActualTypeArguments(); // 强制转型为带参数的泛型类型，
            // getActualTypeArguments()方法获取类型中的实际类型，如map<String,Integer>中的
            // String，integer因为可能是多个，所以使用数组
            return Arrays.asList(typesto);
        }
        return new ArrayList();
    }


}
