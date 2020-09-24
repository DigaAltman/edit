package cn.bbzzzs.test.sql.pojo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
public class Course {
    private Integer courseId;
    private String courseName;
    private Integer courseVersion;
    private List<Student> student;
}
