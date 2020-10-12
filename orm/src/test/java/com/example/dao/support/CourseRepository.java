package com.example.dao.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CourseRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Course> findById(Integer courseId) {
        List<Course> courseList = jdbcTemplate.query("select * from course where course_id=?",new BeanPropertyRowMapper<Course>(Course.class), courseId);
        return courseList;
    }



}
