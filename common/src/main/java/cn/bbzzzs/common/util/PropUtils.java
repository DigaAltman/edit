package cn.bbzzzs.common.util;

import org.apache.commons.beanutils.BeanUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Properties;

/**
 * created by TMT
 */
public class PropUtils {

    /**
     * 相对路径名称
     */
    private static final String CLASSPATH_SUFFIX = "classpath:";

    /**
     * 根据流来获取 properties 对象
     *
     * @param is
     * @return
     */
    public static KV load(InputStream is) {
        Properties properties = new Properties();
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new KV(properties);
    }

    /**
     * 根据文件来获取 properties 对象
     *
     * @param file
     * @return
     */
    public static KV load(File file) {
        try {
            return load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        throw new IllegalArgumentException("get KV errors, by FileNotFoundException..");
    }

    /**
     * 根据 相对路径 来获取 properties 对象
     *
     * @param classpath
     * @return
     */
    public static KV load(String classpath) {
        if (classpath.contains(CLASSPATH_SUFFIX)) {
            classpath = classpath.substring(CLASSPATH_SUFFIX.length());
        }

        return load(ClassUtils.getClassLoader().getResourceAsStream(classpath));
    }


    public static class KV {

        private Properties properties;

        public KV(Properties properties) {
            this.properties = properties;
        }

        /**
         * 推荐的,底层支持方法
         *
         * @param key          键
         * @param defaultValue 默认值
         * @param <T>          默认值类型
         * @return 在指定过的properties中寻找指定key对应的val, 如果找不到就返回默认值
         */
        public <T> T get(String key, T defaultValue) {
            try {
                T val = (T) properties.get(key);
                return val == null ? defaultValue : val;
            } catch (Exception e) {
                return defaultValue;
            }
        }


        /*-----------------------------------------------------*/
        /*-------------- 带默认值的get操作,推荐使用 ---------------*/
        /*-----------------------------------------------------*/

        public String getString(String key, String defaultValue) {
            return get(key, defaultValue);
        }

        public Boolean getBoolean(String key, Boolean defaultValue) {
            return get(key, defaultValue);
        }

        public Long getLong(String key, Long defaultValue) {
            return get(key, defaultValue);
        }

        public Short getShort(String key, Short defaultValue) {
            return get(key, defaultValue);
        }

        public Float getFloat(String key, Float defaultValue) {
            return get(key, defaultValue);
        }

        public Double getDouble(String key, Double defaultValue) {
            return get(key, defaultValue);
        }

        public Character getChar(String key, Character defaultValue) {
            return get(key, defaultValue);
        }

        public Byte getByte(String key, Byte defaultValue) {
            return get(key, defaultValue);
        }

        public Integer getInteger(String key, Integer defaultValue) {
            return get(key, defaultValue);
        }

        /*========================================================================*/
        /*========================== 不带默认值的方法 ==============================*/
        /*========================================================================*/


        public String getString(String key) {
            return get(key, (String) null);
        }

        public Boolean getBoolean(String key) {
            return get(key, (Boolean) null);
        }

        public Long getLong(String key) {
            return get(key, (Long) null);
        }

        public Short getShort(String key) {
            return get(key, (Short) null);
        }

        public Float getFloat(String key) {
            return get(key, (Float) null);
        }

        public Double getDouble(String key) {
            return get(key, (Double) null);
        }

        public Character getChar(String key) {
            return get(key, (Character) null);
        }

        public Byte getByte(String key) {
            return get(key, (Byte) null);
        }

        public Integer getInteger(String key) {
            return get(key, (Integer) null);
        }


        /**
         * 将properties中的属性转换为对应的实体类
         *
         * @param prefix properties的前缀, 如果没有前缀. 则传入 null
         * @param clazz  实体类类型
         * @param <T>
         * @return 返回转换后的实体类
         */
        public <T> T build(String prefix, Class<T> clazz) {
            try {
                T instance = clazz.newInstance();
                ReflexUtils.fieldList(clazz).forEach(field -> {
                    String fieldName = field.getName();
                    String propKey = fieldName;
                    if(prefix != null) {
                        propKey = prefix + "." + fieldName;
                    }
                    Class fieldType = field.getType();

                    Object value = null;
                    if (fieldType == int.class || Integer.class.isAssignableFrom(fieldType)) {
                        value = getInteger(propKey);
                    } else if (fieldType == double.class || Double.class.isAssignableFrom(fieldType)) {
                        value = getDouble(propKey);
                    } else if (fieldType == float.class || Float.class.isAssignableFrom(fieldType)) {
                        value = getFloat(propKey);
                    } else if (fieldType == long.class || Long.class.isAssignableFrom(fieldType)) {
                        value = getLong(propKey);
                    } else if (fieldType == short.class || Short.class.isAssignableFrom(fieldType)) {
                        value = getShort(propKey);
                    } else if (fieldType == boolean.class || Boolean.class.isAssignableFrom(fieldType)) {
                        value = getBoolean(propKey);
                    } else if (fieldType == char.class || Character.class.isAssignableFrom(fieldType)) {
                        value = getChar(propKey);
                    } else if (fieldType == byte.class || Byte.class.isAssignableFrom(fieldType)) {
                        value = getByte(propKey);
                    } else if (Date.class.isAssignableFrom(fieldType)) {
                        value = new Date(getLong(propKey));
                    } else {
                        value = getString(propKey);
                    }
                    try {
                        BeanUtils.copyProperty(instance, fieldName, value);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
                return instance;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            return null;
        }


        public <T> T build(Class<T> clazz) {
            return build(null, clazz);
        }

    }

}
