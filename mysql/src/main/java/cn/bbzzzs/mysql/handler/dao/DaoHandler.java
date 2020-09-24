package cn.bbzzzs.mysql.handler.dao;

import cn.bbzzzs.mysql.pojo.DataBase;

import java.util.List;
import java.util.Map;

public interface DaoHandler {

    /**
     * 针对不同的DAO层的处理方案
     *
     * @param dataBase  数据库对象
     * @param tableName 表名称
     * @return
     */
    Map<String, List> handle(DataBase dataBase, String tableName);

}
