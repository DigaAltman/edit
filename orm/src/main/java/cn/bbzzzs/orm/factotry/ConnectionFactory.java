package cn.bbzzzs.orm.factotry;

import cn.bbzzzs.orm.pojo.DataBase;
import cn.bbzzzs.orm.util.SQLUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

/**
 * 连接工厂,负责构建 SQL 连接
 */
@FunctionalInterface
public interface ConnectionFactory {
    /**
     * 会话级别 connection 缓存
     */
    ThreadLocal<Map<String, Connection>> map = new ThreadLocal();

    ConnectionFactory DEFAULT_CONNECTION_FACTORY = dataBase -> {
        String url = SQLUtil.buildUrl(dataBase);
        Connection connection = map.get().get(url);
        if(connection == null) {
            try {
                connection = DriverManager.getConnection(url, dataBase.getUsername(), dataBase.getPassword());
                map.get().put(url, connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return map.get().get(url);
    };

    /**
     * 构建连接
     * @param dataBase
     * @return
     */
    Connection buildConnection(DataBase dataBase);

}
