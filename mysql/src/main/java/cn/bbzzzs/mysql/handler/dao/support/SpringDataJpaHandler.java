package cn.bbzzzs.mysql.handler.dao.support;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.mysql.handler.dao.DaoHandler;
import cn.bbzzzs.mysql.pojo.DataBase;

import java.util.List;
import java.util.Map;

public class SpringDataJpaHandler implements DaoHandler {

    @Override
    public Map<String, List> handle(DataBase dataBase, String tableName) {
        return null;
    }


    /**
     * 生成 mybatis 的 mapper 代码
     * @return
     */
    private String generateMapperCode() {
        StringUtils.SBuilder sb = new StringUtils.SBuilder();

        StringUtils.SBuilder packageValue = StringUtils.builder();
        packageValue.build("package com.example.dao;\n\n");

        StringUtils.SBuilder importValue = StringUtils.builder();
        importValue.build("import com.example.pojo.Course;\n");
        importValue.build("import org.springframework.data.jpa.repository.JpaRepository;\n");
        importValue.build("import org.springframework.data.jpa.repository.JpaSpecificationExecutor;\n\n");

        StringUtils.SBuilder codeValue = StringUtils.builder();

        codeValue.build("/**\n");
        codeValue.build("' * @author edit \n");
        codeValue.build(" * @desc 课程持久类\n");
        codeValue.build(" */\n");
        codeValue.build("public interface CourseRepository<Course> extends JpaRepository<Course,Integer>, JpaSpecificationExecutor<Course> { \n\n");
        codeValue.build("}\n");


        return sb.build(packageValue.toString(), importValue.toString(), codeValue.toString()).toString();
    }
}
