package cn.bbzzs.db.test.pojo;

import cn.bbzzzs.db.annotation.Id;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Course implements Serializable {
    @Id
    private Integer courseId;
    private String courseName;
    private Integer courseVersion;
    private Date createTime;
}
