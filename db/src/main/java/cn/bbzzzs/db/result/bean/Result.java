package cn.bbzzzs.db.result.bean;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 处理结果映射中的每个字段和 SQL 返回的字段的映射关系
 */
@Data
@Accessors(chain = true)
public class Result {
    /**
     * SQL 返回结果的字段名称
     */
    protected String column;

    /**
     * 对应的实体类的属性
     */
    protected String property;

    /**
     * 主键标识
     */
    protected boolean isPrimary;

    /**
     * 字段类型
     */
    protected Class type;

    /**
     * 懒加载
     */
    protected boolean fetchType = false;

    /**
     * 懒加载对应的 select
     */
    protected String select;

    /**
     * 一对一 关联映射
     */
    protected ResultMap associationMap;

    /**
     * 一对多 关联映射
     */
    protected ResultMap collectionMap;
}
