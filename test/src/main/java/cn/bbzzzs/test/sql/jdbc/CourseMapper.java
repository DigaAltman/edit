package cn.bbzzzs.test.sql.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CourseMapper {

    @Autowired
    private JdbcTemplate jdbcTemplate;



}
