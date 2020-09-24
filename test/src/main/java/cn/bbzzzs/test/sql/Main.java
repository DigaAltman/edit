package cn.bbzzzs.test.sql;

import cn.bbzzzs.xml.$;

import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {
        $ $ = new $(new File("D:\\java_project\\2018\\05\\edit\\test\\src\\main\\resources\\StudentMapper.xml"));
        $.tag("li").html("<h1>已经被河蟹了!!!</h1>");

    }

}
