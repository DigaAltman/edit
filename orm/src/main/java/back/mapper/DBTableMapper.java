//package back.mapper;
//
//import back.annotation.Param;
//import back.annotation.Select;
//import back.entity.DBTable;
//
//import java.util.List;
//
///**
// * created by TMT
// */
//public interface DBTableMapper {
//
//    @Select("SELECT `table_name`, `table_comment` FROM information_schema.TABLES WHERE table_schema = '${db}' ORDER BY table_name")
//    List<DBTable> selectTableList(@Param("db") String db);
//
//}
