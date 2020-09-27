package cn.bbzzzs.mysql.handler.dao.support.mapper.mybatis;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.mysql.common.KeyEnum;
import cn.bbzzzs.mysql.handler.dao.support.AbstractMapper;
import cn.bbzzzs.mysql.pojo.TableDetail;
import cn.bbzzzs.mysql.service.TableService;
import cn.bbzzzs.mysql.vo.TableDetailVo;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class Mapper extends AbstractMapper {

    @Override
    protected Map mapperCode(StringUtils.SBuilder sb, TableDetail tableDetail, List<TableDetailVo> tableDetailVoList) {
        // 解析 mapper.xml
        String mapperXml = generateMapperXml(tableDetail, tableDetailVoList);

        // 解析 mapper.java
        String mapperCode = generateMapperCode(tableDetail, tableDetailVoList);

        // 获取表对应的类的名称
        String name = StringUtils.humpFirstUpper(tableDetail.getName());
        LinkedHashMap codeMap = new LinkedHashMap();
        codeMap.put(name + "Mapper.xml", mapperXml);
        codeMap.put(name + "Mapper.java", mapperCode);

        return codeMap;
    }

    /**
     * 生产 mybatis 的 mapper.java 文件
     *
     * @param tableDetail
     * @param tableDetailVoList
     * @return
     */
    private String generateMapperCode(TableDetail tableDetail, List<TableDetailVo> tableDetailVoList) {
        // TODO 逻辑带实现
        return null;
    }

    /**
     * 生成 mybatis 的 mapper.xml 文件
     *
     * @return
     */
    private String generateMapperXml(TableDetail tableDetail, List<TableDetailVo> tableDetailVoList) {
        String tableName = tableDetail.getName();
        String name = StringUtils.humpFirstUpper(tableName);
        String namespace = String.format("com.example.mapper.%sMapper", name);

        StringUtils.SBuilder sb = new StringUtils.SBuilder();
        String resultMap = name + "Map";
        sb
                .build("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .build("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n")
                .build("<mapper namespace=\"").build(namespace).build("\">\n")

                // 解析 <resultMap></resultMap>
                .build("  <resultMap id=\"").build(resultMap).build("\" type=\"com.example.pojo.").build(name).build("\">\n");

        // 循环遍历字段, 根据字段索引类型进行判断.
        tableDetailVoList.forEach(field -> {
            String key = field.getKey();
            String columnName = field.getFieldName();
            if (!StringUtils.isEmpty(key)) {
                sb.build("        <!-- 索引列[keyType:").build(key, " - keyName:", field.getKeyName()).build("], 建议使用此参数作为条件查询 -->\n");
                KeyEnum keyEnum = KeyEnum.valueOf(key);

                if (keyEnum == KeyEnum.PRI) {
                    sb.build("        <id property=\"").build(StringUtils.hump(columnName)).build("\" column=\"").build(columnName).build("\"/>\n");
                }
            }

        });

        sb.build("  </resultMap>\n\n");


        // 定义 <sql> 片段
        sb.build("    <sql id=\"baseColumn\">\n");
        for (int i = 0; i < tableDetailVoList.size(); i++) {
            sb.build(tableDetailVoList.get(i).getFieldName());
            if (i != tableDetailVoList.size() - 1) {
                sb.build(",");
            }
        }
        sb.build("\n");
        sb.build("    </sql>\n");

        // 定义常用的基于主键的 CRUD 方法
        List<TableDetailVo> priList = tableDetailVoList.stream().filter(v -> KeyEnum.valueOf(v.getKey()) == KeyEnum.PRI).collect(Collectors.toList());

        if (priList.size() > 0) {
            // 存在主键的情况下使用
            TableDetailVo primaryKey = priList.get(0);
            String id = primaryKey.getFieldName();
            String primaryKeyName = StringUtils.hump(id);
            String parameterType = TableService.sqlTypeToJavaType(primaryKey.getFieldType()).getName();
            sb
                    .build("    <!-- 根据 ").build(!StringUtils.isEmpty(primaryKey.getComment()) ? primaryKey.getComment() : primaryKeyName).build(" 查询数据-->\n")
                    .build("    <select id=\"selectById\" resultMap=\"").build(resultMap).build("\" parameterType=\"").build(parameterType).build(")\">\n")
                    .build("        select <include refid=\"baseColumn\"/> from ").build(tableName).build(" where ").build(id).build(" = #{").build(primaryKeyName).build("}")
                    .build("    </select>\n\n")

                    .build("    <!-- 新增 方法 -->\n")
                    .build("    <insert id=\"insert\" parameterType=\"").build(parameterType).build("\">\n");
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
        }

        // 处理唯一索引

        // 处理普通索引或者联合索引

        sb.build("</mapper>\n");
        return sb.toString();
    }
}
