package cn.bbzzzs.mysql.pojo;

import cn.bbzzzs.db.result.bean.Result;
import cn.bbzzzs.db.result.bean.ResultMap;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TableFieldComment implements Serializable {
    private String columnName;
    private String columnComment;
    private String columnType;
    private String columnKey;

    public static ResultMap resultMap() {
        ResultMap resultMap = new ResultMap();
        List<Result> resultList = resultMap.setId("TableFieldComment").setType(TableFieldComment.class.getName())
                .getResultList();
        resultList.add(new Result().setColumn("COLUMN_NAME").setProperty("columnName"));
        resultList.add(new Result().setColumn("column_comment").setProperty("columnComment"));
        resultList.add(new Result().setColumn("column_type").setProperty("columnType"));
        resultList.add(new Result().setColumn("column_key").setProperty("columnKey"));
        return resultMap;
    }
}
