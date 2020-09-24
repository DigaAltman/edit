package cn.bbzzzs.db.factory;

import cn.bbzzzs.db.result.factory.ResultMapFactory;
import cn.bbzzzs.db.result.factory.support.DefaultResultMapFactory;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * DB 工厂
 */
@Data
public class DBFactory {
    private Connection connection;

    private ResultMapFactory factory = new DefaultResultMapFactory();

    private DBFactory() {

    }

    public DBFactory(Connection connection) {
        this.connection = connection;
    }

    /**
     * 获取 DB 对象
     *
     * @return
     */
    public DB getDBInstance() {
        return new DB(connection, factory);
    }

    /**
     * 关闭连接
     */
    public void close() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
