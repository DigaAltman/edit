package cn.bbzzzs.common.util;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created By TMT
 */
public class StringUtils {

    /**
     * 将所有的字符串拼接起来,然后返回结果
     *
     * @param str1 字符串1
     * @param strs 字符串2,字符串3,字符串3 ...
     * @return 返回拼接后的结果
     */
    public static String concat(String str1, String... strs) {
        StringBuilder sb = new StringBuilder(str1);
        for (String str : strs) {
            sb.append(str);
        }
        return sb.toString();
    }


    /**
     * 判断字符串是否为null
     *
     * @param str 字符串
     * @return 如果字符串是空格, 则返回false
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

    /**
     * 判断字符串是否包含内容, 如果字符串是空格,则返回false
     *
     * @param str 字符串
     * @return
     */
    public static boolean hasLength(String str) {
        return str != null && str.trim().length() > 0;
    }


    /**
     * 首字母大写的驼峰转换
     *
     * @param name
     * @return
     */
    public static String humpFirstUpper(String name) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name);
    }

    public static String reverseHump(String str) {
        if (str.length() == 1) {
            return str;
        }
        char c = str.charAt(0);
        if (isUpperStr(c)) {
            return "_" + (char) (c + 32) + reverseHump(str.substring(1));
        }
        return c + reverseHump(str.substring(1));
    }

    /**
     * 驼峰转换
     *
     * @param name
     * @return
     */
    public static String hump(String name) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name);
    }

    /**
     * 首字母大写
     *
     * @param name
     * @return
     */
    public static String firstUpper(String name) {
        char[] ch = name.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    /**
     * 字符串拼接
     *
     * @param s
     * @return
     */
    public static SBuilder builder(String... s) {
        return new SBuilder().build(s);
    }

    public static class SBuilder {
        private List<String> str = new LinkedList();

        public SBuilder build(String... s) {
            Arrays.stream(s).forEach(str::add);
            return this;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            str.forEach(sb::append);
            return sb.toString();
        }
    }

    /**
     * 反转字符串
     *
     * @param str 字符串
     * @return 返回反转后的字符串
     */
    public static String reverse(String str) {
        if (str.length() == 1) {
            return str;
        }

        // 返回最后一个字符 + reverse(剩余的字符)
        return str.substring(str.length() - 1) + reverse(str.substring(0, str.length() - 1));
    }

    /**
     * 判断字符是否为大写
     *
     * @param c 字符
     * @return
     */
    public static boolean isUpperStr(char c) {
        return c >= 'A' && c <= 'Z';
    }

    /**
     * 字符转换为小写
     *
     * @param c 字符
     * @return
     */
    public static char toLower(char c) {
        return (char) (c + 32);
    }

    public static String lowerFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    /**
     * 分词器, 去除文本中的空格
     *
     * @param val
     * @return
     */
    public static List<String> participle(String val) {
        List<String> wordList = Lists.newLinkedList();
        StringBuilder sb = new StringBuilder();
        val = val + " ";
        for (int i = 0; i < val.length(); i++) {
            if (val.charAt(i) == ' ' || i == val.length() - 1) {
                if (sb.length() != 0) {
                    wordList.add(sb.toString());
                    sb.delete(0, sb.length());
                }
            } else {
                sb.append(val.charAt(i));
            }
        }
        return wordList;
    }
}
