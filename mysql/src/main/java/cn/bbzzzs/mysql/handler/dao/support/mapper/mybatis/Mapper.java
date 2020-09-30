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
        // 管理 package 语句
        StringUtils.SBuilder packageText = new StringUtils.SBuilder();
        // 管理 import 语句
        Set<Class> importClassSet = new LinkedHashSet();
        // 管理 核心 语句
        StringUtils.SBuilder appText = new StringUtils.SBuilder();

        packageText.build("package com.example.mapper;\n\n");


        // 实体类名称
        String pojoName = StringUtils.humpFirstUpper(tableDetail.getName());

        // 实体类对应的属性名称
        String paramName = StringUtils.hump(tableDetail.getName());

        // 类名称
        String className = pojoName + "Mapper";
        appText.build("public class ", className, "{\n\n");

        // 生成插入的方法
        appText
                .build("    /**")
                .build("     * @param ", paramName, "需要添加的数据")
                .build("     * @return 返回数据库影响条数")
                .build("     */")
                .build("    int insert(", pojoName, " ", paramName, ");\n\n");

        // 判断这个类是否存在主键, 如果存在主键. 那么我们就可以根据主键进行 查询, 修改
        List<TableDetailVo> priList = tableDetailVoList.stream().filter(detailVo -> !StringUtils.isEmpty(detailVo.getKey()) && KeyEnum.valueOf(detailVo.getKey()) == KeyEnum.PRI).collect(Collectors.toList());
        if (priList.size() > 0) {
            // 存在主键的情况下使用
            TableDetailVo primaryKey = priList.get(0);
            // 主键的名称
            String primaryKeyName = StringUtils.hump(primaryKey.getFieldName());
            // 主键对应的 java 类型
            Class idClass = TableService.sqlTypeToJavaType(primaryKey.getFieldType());

            importClassSet.add(idClass);

            // 生成查询方法
            appText
                    .build("    /**\n")
                    .build("     * @param ", primaryKeyName, " 字段主键. 使用这个字段作为查询条件能命中主键索引\n")
                    .build("     * @return 返回此 id 对应的数据\n")
                    .build("     */")
                    .build("   ", pojoName, " selectById(", idClass.getSimpleName(), " ", primaryKeyName, ");\n\n");

            // 生成修改的方法
            appText
                    .build("    /**")
                    .build("     * @param ", paramName, "需要修改的数据, 他会根据主键进行内容修改")
                    .build("     * @return 返回数据库影响条数")
                    .build("     */")
                    .build("    int updateById(", pojoName, " ", paramName, ");\n\n");
        }

        // 处理唯一索引, 基于唯一索引生成 [查询语句]
        tableDetailVoList.stream().filter(td -> StringUtils.isEmpty(td.getKey()) && KeyEnum.valueOf(td.getKey()) == KeyEnum.UNI).forEach(unionKeyDetail -> {
            // mappedStatement 的id组成: 查询标识(select) + By + 字段名称
            String mappedStatementId = "selectBy" + StringUtils.humpFirstUpper(unionKeyDetail.getFieldName());
            Class fieldClass = TableService.sqlTypeToJavaType(unionKeyDetail.getFieldType());
            importClassSet.add(fieldClass);

            appText.build(pojoName, " ", mappedStatementId, "(",fieldClass.getSimpleName(), " ", StringUtils.hump(unionKeyDetail.getFieldName()),");\n\n");
        });

        // 处理普通索引或者联合索引
        List<TableDetailVo> mulList = tableDetailVoList.stream().filter(td -> StringUtils.isEmpty(td.getKey()) && KeyEnum.valueOf(td.getKey()) == KeyEnum.MUL).collect(Collectors.toList());
        // 一个联合索引中可能包括多个字段,所以我们用个map来表示, key是索引名称, val是索引列表
        Map<String, List<TableDetailVo>> mulMap = new LinkedHashMap();
        for (TableDetailVo tableDetailVo : mulList) {
            List<TableDetailVo> innerList = mulMap.get(tableDetailVo.getKeyName());

            // 做一个 null 判断
            if (innerList == null) {
                innerList = new LinkedList();
                mulMap.put(tableDetailVo.getKeyName(), innerList);
            }

            innerList.add(tableDetailVo);
        }

        // 遍历 map, 生成 组合 查询. mulIndex 表示索引名称, fieldList 表示此索引对应的索引字段集合
        mulMap.forEach((mulIndex, fieldList) -> {
            // 方法名称中的字段名称
            StringUtils.SBuilder fieldName = new StringUtils.SBuilder();
            // 参数注释
            StringUtils.SBuilder paramComment = new StringUtils.SBuilder();
            // 字段注释
            StringUtils.SBuilder fieldComment = new StringUtils.SBuilder();
            // 参数
            StringUtils.SBuilder paramBuilder = new StringUtils.SBuilder();

            for (int i = 0; i < fieldList.size(); i++) {
                TableDetailVo tableDetailVo = fieldList.get(i);
                fieldName.build(StringUtils.humpFirstUpper(tableDetailVo.getFieldName()));
                Class fieldClass = TableService.sqlTypeToJavaType(tableDetailVo.getFieldType());

                importClassSet.add(fieldClass);

                paramBuilder.build(fieldClass.getSimpleName(), " ", StringUtils.hump(tableDetailVo.getFieldName()));
                fieldComment.build(StringUtils.isEmpty(fieldList.get(i).getComment()) ? fieldList.get(i).getFieldName() : fieldList.get(i).getComment());
                paramComment.build("    * @param ", StringUtils.hump(fieldList.get(i).getFieldName()), "\n");
                if (i != fieldList.size() - 1) {
                    fieldName.build("And");
                    fieldComment.build("和");
                    paramBuilder.build(",");
                }
            }




            String mappedStatementId = "selectBy" + fieldName.toString();
            appText
                    .build("    /**\n")
                    .build("     * 根据 ", fieldComment.toString(), " 查询数据, 走组合索引\n")
                    .build(paramComment.toString())
                    .build("     * @return 返回符合条件的数据\n")
                    .build("     */\n")
                    .build("    ", pojoName, " ", mappedStatementId, "(", paramBuilder.toString(), ")\n");
        });

        appText.build("}\n");

        return null;
    }


    /**
     * 生成 mybatis 的 mapper.xml 文件
     *
     * @return
     */
    private String generateMapperXml(TableDetail tableDetail, List<TableDetailVo> tableDetailVoList) {
        // 得到此 mapper 文件的命名空间 <mapper namespace="xxx"></mapper>
        String namespace = String.format("com.example.mapper.%sMapper", tableDetail.getTableName());

        StringUtils.SBuilder sb = new StringUtils.SBuilder();

        // 得到 <resultMap id="xxx"> </resultMap>
        String resultMap = StringUtils.hump(tableDetail.getName()) + "Map";
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
                } else {
                    sb.build("        <result property=\"", StringUtils.hump(columnName), "\" column=\"", columnName, "\"/>\n");
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
        List<TableDetailVo> priList = tableDetailVoList.stream().filter(v -> !StringUtils.isEmpty(v.getKey()) && KeyEnum.valueOf(v.getKey()) == KeyEnum.PRI).collect(Collectors.toList());

        sb.build("    <!-- 新增 方法 -->\n")
                .build("    <insert id=\"insert\" parameterType=\"").build("com.example.pojo.", name).build("\">\n")
                .build("        insert into ").build(tableName).build("\n");

        // 循环遍历字段, 然后添加条件判断进去 names()
        tableDetailVoList.forEach(detail -> {
            sb
                    .build("        <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n")
                    .build("            <if test=\"", StringUtils.hump(detail.getFieldName()), "!= null\">", detail.getFieldName(), ",</if>\n")
                    .build("        </trim>\n");
        });

        // values()
        tableDetailVoList.forEach(detail -> {
            sb
                    .build("        <trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">\n")
                    .build("            <if test=\"", StringUtils.hump(detail.getFieldName()), "!= null\">#{", detail.getFieldName(), "},</if>\n")
                    .build("        </trim>\n");
        });

        sb.build("    </insert>\n\n");

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
                    .build("    </select>\n\n");


            sb
                    .build("    <!-- 根据条件 修改 数据方法 -->\n")
                    .build("    <update id=\"updateById\" parameterType=\"", parameterType, "\">\n");
            // 根据部分字段进行修改的方法
            tableDetailVoList.forEach(detail -> {
                sb.build("        update ", tableName, "\n")
                        .build("        <set>\n")
                        .build("            <if test=\"", StringUtils.hump(detail.getFieldName()), " != null\">", detail.getFieldName(), " = #{", StringUtils.hump(detail.getFieldName()), "},</if>")
                        .build("        </set>\n")
                        .build("        where ", id, "= #{", primaryKeyName, "}\n");
            });
            sb
                    .build("    </update>\n\n");

            // 暂时不提供物理删除, 希望用户使用逻辑删除


            sb
                    .build("    <!-- 根据 课程版本信息和课程id 查询数据, 走 联合 索引 -->\n")
                    .build("    <select id=\"selectByTeacherIdAndCourseVersion\" resultMap=\"courseMap\" parameterType=\"map\">\n")
                    .build("        select <include refid=\"baseColumn\"/> from course c where c.teacher_id = #{teacherId} and c.course_version = #{courseVersion}")
                    .build("    </select>\n\n");
        }

        // 处理唯一索引, 基于唯一索引生成 [查询语句]
        tableDetailVoList.stream().filter(td -> StringUtils.isEmpty(td.getKey()) && KeyEnum.valueOf(td.getKey()) == KeyEnum.UNI).forEach(unionKeyDetail -> {
            // mappedStatement 的id组成: 查询标识(select) + By + 字段名称
            String mappedStatementId = "selectBy" + StringUtils.humpFirstUpper(unionKeyDetail.getFieldName());
            String fieldParameterType = TableService.sqlTypeToJavaType(unionKeyDetail.getFieldType()).getName();
            sb
                    .build("    <!-- 根据 ", StringUtils.isEmpty(unionKeyDetail.getComment()) ? unionKeyDetail.getFieldName() : unionKeyDetail.getComment(), " 查询数据, 走唯一索引 -->\n")
                    .build("    <select id=\"", mappedStatementId, "\" resultMap=\"", resultMap, "\" parameterType=\"", fieldParameterType, "\">\n")
                    .build("        select <include refid=\"baseColumn\"/> from ", tableName, " where ", unionKeyDetail.getFieldName(), " = #{", StringUtils.hump(unionKeyDetail.getFieldName()), "}")
                    .build("    </select>\n\n");
        });

        // 处理普通索引或者联合索引
        List<TableDetailVo> mulList = tableDetailVoList.stream().filter(td -> StringUtils.isEmpty(td.getKey()) && KeyEnum.valueOf(td.getKey()) == KeyEnum.MUL).collect(Collectors.toList());

        // 一个联合索引中可能包括多个字段,所以我们用个map来表示, key是索引名称, val是索引列表
        Map<String, List<TableDetailVo>> mulMap = new LinkedHashMap();
        for (TableDetailVo tableDetailVo : mulList) {
            List<TableDetailVo> innerList = mulMap.get(tableDetailVo.getKeyName());

            // 做一个 null 判断
            if (innerList == null) {
                innerList = new LinkedList();
                mulMap.put(tableDetailVo.getKeyName(), innerList);
            }

            innerList.add(tableDetailVo);
        }

        // 遍历 map, 生成 组合 查询. mulIndex 表示索引名称, fieldList 表示此索引对应的索引字段集合
        mulMap.forEach((mulIndex, fieldList) -> {
            // mappedStatement 的id组成: 查询标识(select) + By + a字段 + And + b字段
            StringUtils.SBuilder fieldName = new StringUtils.SBuilder();
            StringUtils.SBuilder fieldComment = new StringUtils.SBuilder();
            for (int i = 0; i < fieldList.size(); i++) {
                fieldName.build(StringUtils.humpFirstUpper(fieldList.get(i).getFieldName()));
                fieldComment.build(StringUtils.isEmpty(fieldList.get(i).getComment()) ? fieldList.get(i).getFieldName() : fieldList.get(i).getComment());
                if (i != fieldList.size() - 1) {
                    fieldName.build("And");
                    fieldComment.build("和");
                }
            }

            String mappedStatementId = "selectBy" + fieldName.toString();
            sb
                    .build("    <!-- 根据 ", fieldComment.toString(), " 查询数据, 走" + mulIndex + "索引 -->\n")
                    .build("    <select id=\"", mappedStatementId, "\" resultMap=\"", resultMap, "\" parameterType=\"map\">\n")
                    .build("        select <include refid=\"baseColumn\"/> from ", tableName, " where ");

            // 需要遍历字段集合, 将条件循环添加进去
            for (int i = 0; i < fieldList.size(); i++) {
                TableDetailVo f = fieldList.get(i);
                sb.build(f.getFieldName(), " = #{", StringUtils.hump(f.getFieldName()), "}");
                if (i != fieldList.size() - 1) {
                    sb.build(" and ");
                }
            }

            sb
                    .build("    </select>\n\n");
        });


        sb.build("</mapper>\n");
        return sb.toString();
    }
}
