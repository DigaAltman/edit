package cn.bbzzzs.test.sql.pojo;

import lombok.Data;

@Data
public class Teacher {
    private Integer teacherId;
    private String teacherName;
    private Integer teacherVersion;
    private Course course;
}
