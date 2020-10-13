package cn.bbzzzs.orm.pojo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

@Data
public class TableDetail implements Serializable {
    // 数据表名称
    private String Name;

    // 对应的是实体类名称
    private String tableName;

    // 数据表引擎
    private String Engine;

    // 数据表版本号
    private BigInteger Version;

    // 行格式。对于MyISAM引擎，这可能是Dynamic，Fixed或Compressed。动态行的行长度可变，例如Varchar或Blob类型字段。固定行是指行长度不变，例如Char和Integer类型字段。
    private String RowFormat;

    // 表中的行数。对于非事务性表，这个值是精确的，对于事务性引擎，这个值通常是估算的。
    private BigInteger Rows;

    // 平均每行包括的字节数
    private BigInteger AvgRowLength;

    // 整个表中的数据量(字节)
    private BigInteger DataLength;

    // 表可以容纳的最大数据量
    private BigInteger MaxDataLength;

    // 索引占用磁盘的大小
    private BigInteger IndexLength;

    // 对于MyISAM引擎，标识已分配，但现在未使用的空间，并且包含了已被删除行的空间。
    private BigInteger DataFree;

    // 下一个Auto_increment的值
    private BigInteger AutoIncrement;

    // 表的创建时间
    private Date CreateTime;

    // 表的更新时间
    private Date UpdateTime;

    // 使用 check table 或myisamchk工具检查表的最近时间
    private Date CheckTime;

    // 备注
    private String Comment;

    // 字符集
    private String Collation;
}
