package cn.bbzzzs.mysql.util;

import cn.bbzzzs.mysql.pojo.DataBase;

public class SQLUtil {
    public static String buildUrl(DataBase dataBase) {
        String url = String.format("jdbc:mysql://%s:%d/%s", dataBase.getHost(), dataBase.getPort(), dataBase.getDatabase());
        return url;
    }
}
