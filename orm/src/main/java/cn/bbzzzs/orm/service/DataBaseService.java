package cn.bbzzzs.orm.service;

import cn.bbzzzs.db.factory.DB;
import cn.bbzzzs.db.factory.DBFactory;
import cn.bbzzzs.orm.factotry.ConnectionFactory;
import cn.bbzzzs.orm.pojo.DataBase;
import cn.bbzzzs.orm.pojo.TableSize;
import cn.bbzzzs.orm.pojo.ThreadSize;
import cn.bbzzzs.orm.repositoy.DataBaseDao;
import cn.bbzzzs.orm.vo.DataBaseDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;

@Service("cn.bbzzzs.mysql.service.DataBaseService")
public class DataBaseService {

    @Autowired
    private DataBaseDao dataBaseDao;

    /**
     * 获取数据库信息
     * @param dataBase 数据库信息
     */
    public DataBaseDetail getDetail(DataBase dataBase) {
        DataBaseDetail dataBaseDetail = new DataBaseDetail();
        dataBaseDetail.setUsername(dataBase.getUsername());
        dataBaseDetail.setHost(dataBase.getHost());
        dataBaseDetail.setDatabase(dataBase.getDatabase());
        dataBaseDetail.setPort(dataBase.getPort());
        dataBaseDetail.setProduct(dataBase.getProduct());

        /**
         * 构建 Connection
         */
        Connection connection = ConnectionFactory.DEFAULT_CONNECTION_FACTORY.buildConnection(dataBase);
        DBFactory dbFactory = new DBFactory(connection);
        DB db = dbFactory.getDBInstance();

        // 数据库版本
        String version = dataBaseDao.getVersion(db);
        dataBaseDetail.setVersion(version);

        // 数据库字符集
        String charset = dataBaseDao.getCharset(db);

        // 数据库引擎
        String engine = dataBaseDao.getEngine(db);
        dataBaseDetail.setEngine(engine);

        // 数据库大小
        TableSize dataBaseSize = dataBaseDao.getDataBaseSize(db);
        dataBaseDetail.setDbSize(dataBaseSize.getDataSize().add(dataBaseSize.getIndexSize()));

        // 数据占用空间
        dataBaseDetail.setDataSize(dataBaseSize.getDataSize());

        // 索引占用空间
        dataBaseDetail.setIndexSize(dataBaseSize.getIndexSize());

        // 数据库支持最大链接数
        int maxConnection = dataBaseDao.getConnectionNumber(db);
        dataBaseDetail.setMaxConnectionNumber(maxConnection);

        // 数据库当前 缓存线程数 | 创建线程数 | 运行线程数
        ThreadSize threadSize = dataBaseDao.getThreadSize(db);
        dataBaseDetail.setThreadSize(threadSize);

        // 数据库存放数据的目录
        String dir = dataBaseDao.getDir(db);
        dataBaseDetail.setDataDir(dir);

        // 当前登录的用户的权限
        String authority = dataBaseDao.getAuthority(db);
        dataBaseDetail.setAuthority(authority);

        // 当前数据库的缓存区大小
        Long cacheSize = dataBaseDao.getCacheSize(db);
        dataBaseDetail.setCacheSize(cacheSize);

        dataBaseDetail.setPassword(null);
        dbFactory.close();
        return dataBaseDetail;
    }

}
