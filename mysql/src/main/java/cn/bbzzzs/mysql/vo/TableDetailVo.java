package cn.bbzzzs.mysql.vo;

import cn.bbzzzs.mysql.pojo.TableFieldComment;
import cn.bbzzzs.mysql.pojo.TableIndex;
import cn.bbzzzs.mysql.pojo.TableStructure;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 表格详情
 */
@Data
public class TableDetailVo implements Serializable {
    // 字段名称
    private String fieldName;

    // 字段类型
    private String fieldType;

    // 是否允许为Null
    private boolean allowNull;

    // 索引类型, PRI, UNI, MUL
    private String Key;

    // 索引名称
    private String keyName;

    // 字段在索引中的顺序
    private Long seqInIndex;

    // 字段默认值
    private String defaultValue;

    // 索引是否有顺序, A(升序), NULL(无分类)
    private String collation;

    // 索引类型 [FULLTEXT, HASH, BTREE, RTREE]
    private String indexType;

    // 字段备注
    private String comment;

    // 索引备注
    private String indexComment;

    // 字段附加
    private String extra;

    /**
     * @param tableStructure     字段结构
     * @param tableIndexList     字段索引
     * @param tableFieldComments 字段注释
     * @return
     */
    public static TableDetailVo ToVO(TableStructure tableStructure, List<TableIndex> tableIndexList, List<TableFieldComment> tableFieldComments) {
        TableDetailVo detailVo = new TableDetailVo();
        detailVo.setFieldName(tableStructure.getField());
        detailVo.setFieldType(tableStructure.getType());
        detailVo.setAllowNull(tableStructure.getNull().equals("YES"));
        detailVo.setKey(tableStructure.getKey());
        detailVo.setExtra(tableStructure.getExtra());
        detailVo.setDefaultValue(tableStructure.getDefault());

        for (TableIndex tableIndex : tableIndexList) {
            if (tableIndex.getColumnName().equals(tableStructure.getField())) {
                detailVo.setKeyName(tableIndex.getKeyName());
                detailVo.setSeqInIndex(tableIndex.getSeqInIndex());
                detailVo.setCollation(tableIndex.getCollation());
                detailVo.setIndexType(tableIndex.getIndexType());
                detailVo.setIndexComment(tableIndex.getIndexComment());
                break;
            }
        }

        for (TableFieldComment tableFieldComment : tableFieldComments) {
            if(tableFieldComment.getColumnName().equals(tableStructure.getField())) {
                detailVo.setComment(tableFieldComment.getColumnComment());
                break;
            }
        }

        return detailVo;
    }


}
