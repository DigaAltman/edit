//package back.entity;
//
//import cn.bbzzzs.common.annotation.Column;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.io.Serializable;
//import java.util.List;
//
///**
// * 数据表信息,
// * 注意,不能使用 @Access(chain=true) 注解
// */
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class DBTable implements Serializable {
//    /**
//     * 数据表名
//     */
//    @Column("table_name")
//    private String name;
//
//    /**
//     * 数据表备注
//     */
//    @Column("table_comment")
//    private String comment;
//
//    /**
//     * 一对多映射
//     */
//    private List<DBField> dbFieldList;
//}
