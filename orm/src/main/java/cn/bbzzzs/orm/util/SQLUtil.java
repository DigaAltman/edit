package cn.bbzzzs.orm.util;

import cn.bbzzzs.orm.common.DataBaseCommon;
import cn.bbzzzs.orm.pojo.DataBase;


public class SQLUtil {
    /**
     * 基于 DataBase 对象,
     *
     * @param dataBase
     * @return
     */
    public static String buildUrl(DataBase dataBase) {
        try {
            DataBaseCommon.ProductEnum productEnum = DataBaseCommon.ProductEnum.valueOf(dataBase.getProduct());
            switch (productEnum) {
                case MYSQL:
                    return String.format("jdbc:mysql://%s:%d/%s", dataBase.getHost(), dataBase.getPort(), dataBase.getDatabase());
                case ORACLE:
                    return String.format("jdbc:oracle:thin:@//%s:%d/%s", dataBase.getHost(), dataBase.getPort(), dataBase.getDatabase());
                default:
                    return null;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("不支持的产品类型");
        }
    }
}
