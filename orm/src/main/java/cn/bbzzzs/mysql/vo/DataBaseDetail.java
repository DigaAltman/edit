package cn.bbzzzs.mysql.vo;

import cn.bbzzzs.mysql.pojo.DataBase;
import cn.bbzzzs.mysql.pojo.TableSize;
import cn.bbzzzs.mysql.pojo.ThreadSize;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class DataBaseDetail extends DataBase implements Serializable {
    /**
     * 数据库字符集
     */
    private String charset;

    /**
     * 数据库版本
     */
    private String version;

    /**
     * 数据库引擎
     */
    private String engine;

    /**
     * 数据库大小
     */
    private BigDecimal dbSize;

    /**
     * 数据库占用空间
     */
    private BigDecimal dataSize;

    /**
     * 索引占用空间
     */
    private BigDecimal indexSize;

    /**
     * 数据库支持最大链接数
     */
    private Integer maxConnectionNumber;

    /**
     * 线程信息
     */
    private ThreadSize threadSize;

    /**
     * 数据库存放数据的目录
     */
    private String dataDir;

    /**
     * 当前登录的用户的权限
     */
    private String authority;

    /**
     * 当前数据库的缓存区大小
     */
    private Long cacheSize;

}
