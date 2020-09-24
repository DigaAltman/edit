package cn.bbzzzs.common.util;

import java.util.Collection;

public class ArrayUtil {
    /**
     *
     * @param collection
     * @return
     */
    public static String toString(Collection collection) {
        return toString(collection, ",");
    }

    /**
     *
     * @param collection
     * @return
     */
    public static String toString(Collection collection, String prefix) {
        StringUtils.SBuilder sb = StringUtils.builder();
        for (Object o : collection) {
            sb.build(o.toString(), prefix);
        }

        String s = sb.toString();
        return s.length() > prefix.length() ? s.substring(0, s.length() - prefix.length()) : "";
    }
}
