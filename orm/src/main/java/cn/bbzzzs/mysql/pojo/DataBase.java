package cn.bbzzzs.mysql.pojo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class DataBase implements Serializable {
    @NotEmpty(message = "数据库类型不能为空")
    private String product;

    @NotEmpty(message = "数据库地址不能为空")
    private String host;

    @NotNull(message = "端口号不能为空")
    private int port = 3306;

    @NotEmpty(message = "数据库名称不能为空")
    private String database;

    @NotEmpty(message = "用户名不能为空")
    private String username;

    @NotEmpty(message = "密码不能为空")
    private String password;
}