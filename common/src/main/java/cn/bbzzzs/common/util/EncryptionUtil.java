package cn.bbzzzs.common.util;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptionUtil {

    private static MessageDigest md5;
    private static final String DEFAULT_SLAT = "KO NO DIO DA!!";

    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对指定的字符串进行加密
     *
     * @param body 字符串
     * @return 返回加密后内容
     */
    public static String md5(String body) {
        if (body.length() < 6) {
            throw new IllegalArgumentException(" need password body length < 6 !! ");
        }
        byte[] password = md5.digest(StringUtils.reverse(body).getBytes());
        StringBuilder md5code = new StringBuilder(new BigInteger(1, password).toString(16));
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code.append("0");
        }
        return md5code.toString();
    }

    /**
     * 对指定的字符串进行加盐加密, 盐值会放在加密字符串中间
     *
     * @param body 字符串
     * @param slat 盐值
     * @return 返回加密后内容
     */
    public static String md5(String body, String slat) {
        int mid = body.length() / 2;
        return md5(new StringBuilder(StringUtils.reverse(body).substring(0, mid))
                .append(slat).append(StringUtils.reverse(body).substring(mid)).toString());
    }


    /**
     * Base64 解密
     *
     * @param body
     */
    public static String decrypt(String body) {
        return new String(Base64.getDecoder().decode(StringUtils.reverse(body).getBytes())).substring(DEFAULT_SLAT.length());
    }

    /**
     * Base64 加密
     *
     * @param body
     * @return
     */
    public static String encryption(String body) {
        return new String(Base64.getEncoder().encode((DEFAULT_SLAT + StringUtils.reverse(body)).getBytes()));
    }
    
}
