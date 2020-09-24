//package cn.bbzzzs.test.sql;
//
//import cn.bbzzzs.common.db.DBSource;
//import cn.bbzzzs.common.util.DBUtils;
//import cn.bbzzzs.test.sql.pojo.Teacher;
//
//import java.util.List;
//
///**
// * 一对一映射测试
// */
//public class OneToOne {
//
//    public static void main(String[] args) throws Exception {
//
//        DBSource dbSource = DBSource.builder()
//                .driverClassName("com.mysql.jdbc.Driver")
//                .url("jdbc:mysql://47.112.125.251:3306/db1?useSSL=false")
//                .username("db1")
//                .password("db1_1234")
//                .build();
//
//        DBUtils.DB db = DBUtils.build(null);
//
//
//        long startTime = System.currentTimeMillis();
//
//        String sql = "SELECT t.id AS teacher_id, t.name AS teacher_name, t.version AS teacher_version, c.id AS course_id, c.name AS course_name , c.version AS course_version, s.id AS id, s.name, s.sex, s.address , s.course_id, s.version FROM teacher t LEFT JOIN course c ON t.id = c.id LEFT JOIN student s ON c.id = s.course_id";
//
//        List<Teacher> res = db.selectList(sql, Teacher.class);
//
//        long endTime = System.currentTimeMillis();
//        System.out.println(endTime - startTime);
//    }
//
//}
