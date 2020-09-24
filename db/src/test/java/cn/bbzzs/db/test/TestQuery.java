package cn.bbzzs.db.test;

import cn.bbzzs.db.test.pojo.TableDetail;
import cn.bbzzzs.db.factory.DB;
import cn.bbzzzs.db.factory.DBFactory;
import cn.bbzzzs.db.result.bean.Result;
import cn.bbzzzs.db.result.bean.ResultMap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestQuery {
    public static void main(String[] args) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://47.112.125.251:3306/db1?useSSL=false&charset=utf8", "db1", "db1_1234");

        DBFactory dbFactory = new DBFactory(connection);

        DB db = dbFactory.getDBInstance();


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

        TableDetail tableDetail = db.selectOne("show table status where Name=?", resultMap, "course");
        System.out.println(tableDetail);

        db.close();
    }
}



