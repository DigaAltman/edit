package cn.bbzzzs.mysql.repositoy;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.db.factory.DB;
import cn.bbzzzs.db.result.bean.Result;
import cn.bbzzzs.db.result.bean.ResultMap;
import cn.bbzzzs.mysql.pojo.TableDetail;
import cn.bbzzzs.mysql.pojo.TableFieldComment;
import cn.bbzzzs.mysql.pojo.TableIndex;
import cn.bbzzzs.mysql.pojo.TableStructure;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository("cn.bbzzzs.mysql.repositoy.TableDao")
public class TableDao {

    /**
     * 获取数据库下的所有表
     */
    public List<String> getTableList(DB db) {
        return db.selectList("show tables", List.class).stream().map(i -> i.get(0).toString()).collect(Collectors.toList());
    }

    /**
     * 获取数据表的详细信息
     *
     * @param db
     * @param tableName 数据表
     * @return
     */
    public TableDetail detail(DB db, String tableName) {
        // 自定义一个 ResultMap
        ResultMap resultMap = new ResultMap();
        resultMap.setId("tableDetail").setType(TableDetail.class.getName());
        resultMap.getResultList().add(new Result().setColumn("Name").setProperty("Name"));
        resultMap.getResultList().add(new Result().setColumn("Engine").setProperty("Engine"));
        resultMap.getResultList().add(new Result().setColumn("Version").setProperty("Version"));
        resultMap.getResultList().add(new Result().setColumn("Row_format").setProperty("RowFormat"));
        resultMap.getResultList().add(new Result().setColumn("Rows").setProperty("Rows"));
        resultMap.getResultList().add(new Result().setColumn("Avg_row_length").setProperty("AvgRowLength"));
        resultMap.getResultList().add(new Result().setColumn("Data_length").setProperty("DataLength"));
        resultMap.getResultList().add(new Result().setColumn("Max_data_length").setProperty("MaxDataLength"));
        resultMap.getResultList().add(new Result().setColumn("Index_length").setProperty("IndexLength"));
        resultMap.getResultList().add(new Result().setColumn("Data_free").setProperty("DataFree"));
        resultMap.getResultList().add(new Result().setColumn("Auto_increment").setProperty("AutoIncrement"));
        resultMap.getResultList().add(new Result().setColumn("Create_time").setProperty("CreateTime"));
        resultMap.getResultList().add(new Result().setColumn("Update_time").setProperty("UpdateTime"));
        resultMap.getResultList().add(new Result().setColumn("Check_time").setProperty("CheckTime"));
        resultMap.getResultList().add(new Result().setColumn("Comment").setProperty("Comment"));
        resultMap.getResultList().add(new Result().setColumn("Collation").setProperty("Collation"));

        TableDetail tableDetail = db.selectOne("show table status where Name=?", resultMap, tableName);
        tableDetail.setTableName(StringUtils.humpFirstUpper(tableDetail.getName()));
        return tableDetail;
    }

    /**
     * 表结构信息
     */
    public List<TableStructure> getTableStructure(DB db, String tableName) {
        List<TableStructure> tableStructureList = db.selectList(String.format("show columns from %s", tableName), TableStructure.resultMap());
        return tableStructureList;
    }

    /**
     * 表索引信息
     */
    public List<TableIndex> getTableIndex(DB db, String tableName) {
        List<TableIndex> tableIndexList = db.selectList(String.format("show index from %s", tableName), TableIndex.resultMap());
        return tableIndexList;
    }

    /**
     * 表字段备注
     */
    public List<TableFieldComment> getTableFieldComment(DB db, String tableName) {
        List<TableFieldComment> tableFieldCommentList = db.selectList(String.format("SELECT COLUMN_NAME, column_comment, column_type, column_key FROM information_schema.COLUMNS WHERE table_name = '%s'", tableName), TableFieldComment.resultMap());
        return tableFieldCommentList;
    }
}
