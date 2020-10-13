package cn.bbzzzs.orm.pojo;

import cn.bbzzzs.db.result.bean.Result;
import cn.bbzzzs.db.result.bean.ResultMap;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TableIndex implements Serializable {
    private String Table;
    private Long NonUnique;
    private String KeyName;
    private Long SeqInIndex;
    private String ColumnName;
    private String Collation;
    private Long Cardinality;
    private String SubPart;
    private String Packed;
    private String Null;
    private String IndexType;
    private String Comment;
    private String IndexComment;

    public static ResultMap resultMap() {
        ResultMap resultMap = new ResultMap();
        List<Result> resultList = resultMap.setId("TableIndex").setType(TableIndex.class.getName())
                .getResultList();
        resultList.add(new Result().setColumn("Table").setProperty("Table"));
        resultList.add(new Result().setColumn("Non_unique").setProperty("NonUnique"));
        resultList.add(new Result().setColumn("Key_name").setProperty("KeyName"));
        resultList.add(new Result().setColumn("Seq_in_index").setProperty("SeqInIndex"));
        resultList.add(new Result().setColumn("Column_name").setProperty("ColumnName"));
        resultList.add(new Result().setColumn("Collation").setProperty("Collation"));
        resultList.add(new Result().setColumn("Cardinality").setProperty("Cardinality"));
        resultList.add(new Result().setColumn("Sub_part").setProperty("SubPart"));
        resultList.add(new Result().setColumn("Packed").setProperty("Packed"));
        resultList.add(new Result().setColumn("Null").setProperty("Null"));
        resultList.add(new Result().setColumn("Index_type").setProperty("IndexType"));
        resultList.add(new Result().setColumn("Comment").setProperty("Comment"));
        resultList.add(new Result().setColumn("Index_comment").setProperty("IndexComment"));

        return resultMap;
    }



}
