package cn.bbzzzs.mysql.pojo;

import cn.bbzzzs.db.result.bean.Result;
import cn.bbzzzs.db.result.bean.ResultMap;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 表结构信息
 */
@Data
public class TableStructure implements Serializable {
    private String Field;
    private String Type;
    private String Null;
    private String Key;
    private String Default;
    private String Extra;

    /**
     * 获取当前表结构对应的 ResultMap 信息
     * @return
     */
    public static ResultMap resultMap() {
        ResultMap resultMap = new ResultMap();
        List<Result> resultList = resultMap.setId("tableStructure").setType(TableStructure.class.getName())
                .getResultList();
        resultList.add(new Result().setColumn("Field").setProperty("Field"));
        resultList.add(new Result().setColumn("Type").setProperty("Type"));
        resultList.add(new Result().setColumn("Null").setProperty("Null"));
        resultList.add(new Result().setColumn("Key").setProperty("Key"));
        resultList.add(new Result().setColumn("Default").setProperty("Default"));
        resultList.add(new Result().setColumn("Extra").setProperty("Extra"));

        return resultMap;
    }
}


