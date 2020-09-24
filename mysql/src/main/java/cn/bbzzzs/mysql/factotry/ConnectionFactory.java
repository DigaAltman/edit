package cn.bbzzzs.mysql.factotry;

import cn.bbzzzs.mysql.pojo.DataBase;
import cn.bbzzzs.mysql.util.SQLUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 连接工厂,负责构建 SQL 连接
 */
@FunctionalInterface
public interface ConnectionFactory {

    ConnectionFactory MYSQL = dataBase -> {
        String url = SQLUtil.buildUrl(dataBase);
        try {
            Connection connection = DriverManager.getConnection(url, dataBase.getUsername(), dataBase.getPassword());
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    };

    /**
     * 构建连接
     * @param dataBase
     * @return
     */
    Connection buildConnection(DataBase dataBase);
}
