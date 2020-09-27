package cn.bbzzzs.mysql.handler.dao.support;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.mysql.handler.dao.DaoHandler;
import cn.bbzzzs.mysql.pojo.DataBase;

import java.util.List;
import java.util.Map;

public class MybatisHandler implements DaoHandler {

    @Override
    public Map<String, List> handle(DataBase dataBase, String tableName) {

        return null;
    }

    /**
     * 生成 mybatis 的 mapper.xml 文件
     * @return
     */
    private String generateMapperXml() {
        StringUtils.SBuilder sb = new StringUtils.SBuilder();
        sb.build("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        sb.build("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n");
        sb.build("<mapper namespace=\"mapperClassName\">\n");

        // 定义 <resultMap></resultMap>
        sb.build("  <resultMap id=\"courseMap\" type=\"com.example.pojo.Course\">\n");
        sb.build("        <!-- 主键索引[PRIMARY], 建议使用此参数作为条件查询 -->\n");
        sb.build("        <id property=\"courseId\" column=\"course_id\"/>\n");
        sb.build("        <!-- 唯一索引[courseNameKey], 建议使用此参数作为条件查询 -->\n");
        sb.build("        <result property=\"courseName\" column=\"course_name\"/>\n");
        sb.build("        <!-- 联合索引[sindex-1], 建议使用此参数作为条件查询 -->\n");
        sb.build("        <result property=\"courseVersion\" column=\"course_version\"/>\n");
        sb.build("        <!-- 联合索引[sindex-2] -->\n");
        sb.build("        <result property=\"teacherId\" column=\"teacher_id\"/>\n");
        sb.build("        <result property=\"createTime\" column=\"create_time\"/>\n");
        sb.build("  </resultMap>\n\n");

        // 定义 <sql> 片段
        sb.build("    <sql id=\"baseColumn\">\n");
        sb.build("        course_id, course_name, course_version, teacher_id, create_time\n");
        sb.build("    </sql>\n");

        // 定义常用的 CRUD 方法
        sb.build("    <!-- 根据 id 查询 课程 信息 -->\n");
        sb.build("    <select id=\"selectById\" resultMap=\"courseMap\" parameterType=\"java.lang.Integer\">\n");
        sb.build("        select <include refid=\"baseColumn\"/> from course c where c.course_id = #{courseId,jdbcType=INTEGER}");
        sb.build("    </select>\n\n");

        sb.build("    <!-- 根据根据条件 新增 方法 -->\n");
        sb.build("    <insert id=\"insert\" parameterType=\"com.example.pojo.Course\">\n");
        sb.build("        insert into course\n");
        sb.build("        <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        sb.build("            <if test=\"id != null\">id,</if>\n");
        sb.build("        </trim>\n");
        sb.build("        <trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">\n");
        sb.build("            <if test=\"id != null\">#{id,jdbcType=INTEGER},</if>\n");
        sb.build("        </trim>\n");
        sb.build("    </insert>\n\n");

        sb.build("    <!-- 根据根据条件 修改 方法 -->\n");
        sb.build("    <update id=\"updateById\" parameterType=\"com.example.pojo.Course\">\n");
        sb.build("        update course\n");
        sb.build("        <set>\n");
        sb.build("            <if test=\"username != null\">username = #{username,jdbcType=VARCHAR},</if>");
        sb.build("        </set>\n");
        sb.build("        where id = #{id,jdbcType=INTEGER}\n");
        sb.build("    </update>\n\n");

        // 不提供物理删除, 希望用户使用逻辑删除

        // 此时: 当提供主键索引查询, 唯一索引查询, 联合索引查询
        sb.build("    <!-- 根据 课程名称 查询 课程 信息, 走 唯一 索引 -->\n");
        sb.build("    <select id=\"selectByCourseName\" resultMap=\"courseMap\" parameterType=\"java.lang.String\">\n");
        sb.build("        select <include refid=\"baseColumn\"/> from course c where c.course_name = #{courseName,jdbcType=VARCHAR}");
        sb.build("    </select>\n\n");


        sb.build("    <!-- 根据 课程版本信息和课程id 查询 课程 信息, 走 联合 索引 -->\n");
        sb.build("    <select id=\"selectByTeacherIdAndCourseVersion\" resultMap=\"courseMap\" parameterType=\"map\">\n");
        sb.build("        select <include refid=\"baseColumn\"/> from course c where c.teacher_id = #{teacherId} and c.course_version = #{courseVersion}");
        sb.build("    </select>\n\n");

        sb.build("</mapper>\n");
        return sb.toString();
    }

    /**
     * 生成 mybatis 的 mapper 代码
     * @return
     */
    private String generateMapperCode() {
        StringUtils.SBuilder sb = new StringUtils.SBuilder();

        StringUtils.SBuilder packageValue = StringUtils.builder();
        packageValue.build("package com.example.mapper;\n\n");

        StringUtils.SBuilder importValue = StringUtils.builder();
        importValue.build("import com.example.pojo.Cart;\n");

        StringUtils.SBuilder codeValue = StringUtils.builder();

        codeValue.build("public class CourseMapper { \n\n");
        codeValue.build("    /**\n");
        codeValue.build("     * 插入 课程 信息\n");
        codeValue.build("     * @param record 课程信息\n");
        codeValue.build("     * @return 返回被影响的数据行数\n");
        codeValue.build("     */");
        codeValue.build("    int insert(Cart record);\n");
        codeValue.build("}\n");


        return sb.build(packageValue.toString(), importValue.toString(), codeValue.toString()).toString();
    }
}
