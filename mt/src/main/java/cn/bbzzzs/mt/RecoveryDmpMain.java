package cn.bbzzzs.mt;

import cn.bbzzzs.common.util.FileUtils;
import cn.bbzzzs.common.util.LogUtils;
import cn.bbzzzs.common.util.PropUtils;
import cn.bbzzzs.db.factory.DB;
import cn.bbzzzs.db.factory.DBFactory;
import cn.bbzzzs.mt.entity.DmpFile;
import cn.bbzzzs.mt.entity.OracleRecovery;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * 基于 oracle-recovery 程序生成的辅助程序.
 * 简化 ktr 的生成, dmp 文件中的表的解析. 包括配置文件的生件
 *
 *
 */
public class RecoveryDmpMain {

    public static void main(String[] args) throws SQLException, IOException {
        // 1. 解析 application.properties 文件. 得到程序的信息
        PropUtils.KV kv = PropUtils.load(RecoveryDmpMain.class.getResourceAsStream("/application.properties"));
        OracleRecovery oracleRecovery = kv.build("oracle-recovery", OracleRecovery.class);

        Connection connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", oracleRecovery.getHost(), oracleRecovery.getPort(), oracleRecovery.getDatabase()), oracleRecovery.getUsername(), oracleRecovery.getPassword());
        if (connection.isClosed()) {
            throw new IllegalArgumentException("连接不可用...");
        }
        DBFactory dbFactory = new DBFactory(connection);
        DB db = dbFactory.getDBInstance();
        List<String> tableList = db.selectList("show tables", String.class);
        if (tableList.size() == 0) {
            throw new IllegalArgumentException("没有需要导入数据的表...");
        }

        File config = oracleRecovery.getConfigPath();
        File kettleConfig = new File(config.getPath() + File.separator + "kettle-config.properties");
        File recoveryConfig = new File(config.getPath() + File.separator + "oracle-recovery-config.properties");

        // 2. 得到 oracle-recovery\download\inbound 下的所有待处理的dmp数据
        List<DmpFile> handleDmpFileList = oracleRecovery.getHandleDmpFileList();

        String demoKtr = FileUtils.readFile(new File(RecoveryDmpMain.class.getResource("/demo.ktr").getPath()));

        String demoProperties = FileUtils.readFile(new File(RecoveryDmpMain.class.getResource("/kettle.properties").getPath()));

        String dataBaseProperties = FileUtils.readFile(new File(RecoveryDmpMain.class.getResource("/database.properties").getPath()));
        dataBaseProperties = dataBaseProperties
                .replace("#{host}", oracleRecovery.getHost())
                .replace("#{database}", oracleRecovery.getDatabase())
                .replace("#{port}", oracleRecovery.getPort())
                .replace("#{username}", oracleRecovery.getUsername())
                .replace("#{password}", oracleRecovery.getPassword());

        String recoveryProperties = FileUtils.readFile(new File(RecoveryDmpMain.class.getResource("/recovery.properties").getPath()));


        for (DmpFile dmpFile : handleDmpFileList) {
            System.err.println(dmpFile.getFile().getName() + " - " + dmpFile.getID());

            // 3. 解析其中的包含的表信息, 并且生成对应的ktr文件. 然后放在 oracle-recovery\myKettle 下的对应日期下, 然后基于文件名称创建目录, 存放 ktr 文件
            List<String> kettleFileList = dmpFile.getKettleFileList();

            // 获取dmp文件中的所有表
            FileUtils.readLineByNIOSetEncode(dmpFile.getFile(), "GBK", s -> {
                if (s.startsWith("CREATE TABLE")) {
                    int start = s.indexOf("\"");
                    int end = s.substring(start + 1).indexOf("\"");
                    String tableName = s.substring(start + 1, start + 1 + end).toLowerCase();
                    kettleFileList.add(tableName);
                }
            });

            // 生成kettle模板
            kettleFileList.forEach(item -> {
                // TODO 只生成存在于 mysql 中的表对应的 ktr 即可
                if (tableList.contains(item)) {
                    LogUtils.warn("开始生成 表[%s] 对应的 ktr 文件", item);
                    String currentKtr = demoKtr.replace("${name}", item);
                    File file = new File(oracleRecovery.getPath() + File.separator + dmpFile.getKettleDir() + File.separator + item + ".ktr");
                    FileUtils.persistence(currentKtr, file);
                }
            });


            // 4. 生成对应的文件的配置文件, 包括 -kettle.properties , -database.properties , -recovery.properties
            File kettle = new File(config.getPath() + File.separator + dmpFile.getID() + "-kettle.properties");
            FileUtils.persistence(demoProperties.replace("#{ID}", dmpFile.getKettleDir()).replace("#{kettleFileNames}", dmpFile.getKettleFileNames(tableList)), kettle);

            File database = new File(config.getPath() + File.separator + dmpFile.getID() + "-kettle-database.properties");
            FileUtils.persistence(dataBaseProperties, database);

            File recovery = new File(config.getPath() + File.separator + dmpFile.getID() + "-oracle-recovery.properties");
            FileUtils.persistence(recoveryProperties, recovery);

            // 5. 添加配置到 config.properties 中
            String fileName = dmpFile.getFile().getName();
            String regex = fileName.substring(0, fileName.indexOf("."));

            String kettleKV = dmpFile.getID() + "-kettle.properties=^" + regex + ".*\n";
            String databaseKV = dmpFile.getID() + "-kettle-database.properties=^" + regex + ".*\n";
            String recoveryKV = dmpFile.getID() + "-oracle-recovery.properties=^" + regex + ".*\n";

            FileUtils.saveFile(kettleConfig, kettleKV + databaseKV, true);
            FileUtils.saveFile(recoveryConfig, recoveryKV, true);
        }
    }
}
