package cn.bbzzzs.mysql.handler.dao.support.mapper.mybatis;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.common.util.func.DoubleParamVoid;
import cn.bbzzzs.mysql.common.KeyEnum;
import cn.bbzzzs.mysql.handler.dao.support.AbstractMapper;
import cn.bbzzzs.mysql.pojo.TableDetail;
import cn.bbzzzs.mysql.service.TableService;
import cn.bbzzzs.mysql.vo.TableDetailVo;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Mapper extends AbstractMapper {

    @Override
    protected Map mapperCode(StringUtils.SBuilder sb, TableDetail tableDetail, List<TableDetailVo> tableDetailVoList) {
        // 解析 mapper.xml
        String mapperXml = generateMapperXml(tableDetail, tableDetailVoList);

        // 解析 mapper.java
        String mapperCode = generateMapperCode(tableDetail, tableDetailVoList);

        // 获取表对应的类的名称
        String name = tableDetail.getTableName();
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

        packageText.build("package ", super.getMAPPER_PACKAGE_PATH(), ";\n\n");

        // 实体类名称
        String pojoName = StringUtils.humpFirstUpper(tableDetail.getName());

        // 实体类对应的属性名称
        String paramName = StringUtils.hump(tableDetail.getName());

        // 类名称
        String className = pojoName + "Mapper";
        appText.build("public interface ", className, "{\n\n");

        // 生成插入的方法
        appText
                .build("    /**\n")
                .build("     * @param ", paramName, " 需要添加的数据\n")
                .build("     * @return 返回数据库影响条数\n")
                .build("     */\n")
                .build("    int insertSelective(", pojoName, " ", paramName, ");\n\n");

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
                    .build("     */\n")
                    .build("    ", pojoName, " selectById(", idClass.getSimpleName(), " ", primaryKeyName, ");\n\n");

            // 生成修改的方法
            appText
                    .build("    /**\n")
                    .build("     * @param ", paramName, " 需要修改的数据, 他会根据主键进行内容修改\n")
                    .build("     * @return 返回数据库影响条数\n")
                    .build("     */\n")
                    .build("    int updateById(", pojoName, " ", paramName, ");\n\n");
        }

        // 处理唯一索引, 基于唯一索引生成 [查询语句]
        tableDetailVoList.stream().filter(td -> !StringUtils.isEmpty(td.getKey()) && KeyEnum.valueOf(td.getKey()) == KeyEnum.UNI).forEach(unionKeyDetail -> {
            // mappedStatement 的id组成: 查询标识(select) + By + 字段名称
            String mappedStatementId = "selectBy" + StringUtils.humpFirstUpper(unionKeyDetail.getFieldName());
            Class fieldClass = TableService.sqlTypeToJavaType(unionKeyDetail.getFieldType());

            String comment = StringUtils.isEmpty(unionKeyDetail.getComment()) ? unionKeyDetail.getComment() : unionKeyDetail.getFieldName();

            importClassSet.add(fieldClass);
            appText
                    .build("    /**\n")
                    .build("     * @param ", paramName, " 根据", comment, "查询数据, 推荐使用此方法查询数据. 因为它是基于唯一索引查询的\n")
                    .build("     * @return 返回查询出来的数据\n")
                    .build("     */\n");
            appText.build("    ", pojoName, " ", mappedStatementId, "(", fieldClass.getSimpleName(), " ", StringUtils.hump(unionKeyDetail.getFieldName()), ");\n\n");


            String mappedStatementId2 = "containBy" + StringUtils.humpFirstUpper(unionKeyDetail.getFieldName());
            appText
                    .build("    /**\n")
                    .build("     * @param ", paramName, " 根据", comment, "判断数据是否已经存在, 推荐使用此方法来确定数据唯一性. 因为他是基于唯一索引查询的\n")
                    .build("     * @return 返回此数据在数据库中的条数\n")
                    .build("     */\n");
            appText.build("    int ", mappedStatementId2, "(", fieldClass.getSimpleName(), " ", StringUtils.hump(unionKeyDetail.getFieldName()), ");\n\n");

        });

        // 处理普通索引或者联合索引
        List<TableDetailVo> mulList = tableDetailVoList.stream().filter(td -> !StringUtils.isEmpty(td.getKey()) && KeyEnum.valueOf(td.getKey()) == KeyEnum.MUL).collect(Collectors.toList());

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

                paramBuilder.build("", fieldClass.getSimpleName(), " ", StringUtils.hump(tableDetailVo.getFieldName()));
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

        StringUtils.SBuilder returnText = StringUtils.builder(packageText.toString());
        returnText.build("import ", super.getENTITY_PACKAGE_PATH(), ".", pojoName, ";\n");
        importClassSet.stream().filter(clz -> !clz.getName().startsWith("java.lang")).forEach(clz -> returnText.build("import ", clz.getName(), ";\n"));
        returnText.build("\n", appText.toString());

        return returnText.toString();
    }


    /**
     * 生成 ResultMap 内容
     *
     * @param ID              resultMap 的 id
     * @param TYPE            resultMap 的 type
     * @param ID_MAP          resultMap 的 主键字段
     * @param ONLY_MAP        resultMap 的 唯一索引
     * @param KEY_MAP         resultMap 的 索引字段
     * @param RESULT_MAP      resultMap 的 普通字段
     * @param ASSOCIATION_MAP
     * @param COLLECTION_MAP
     * @return 返回一个 <resultMap></resultMap> 文本化的 XML
     */
    private String generateResultMap(
            final String ID,
            final String TYPE,
            final Map<String, String> ID_MAP,
            final Map<String, String> ONLY_MAP,
            final Map<String, String> KEY_MAP,
            final Map<String, String> RESULT_MAP,
            final Map<String, String> ASSOCIATION_MAP,
            final Map<String, String> COLLECTION_MAP) {
        StringUtils.SBuilder sb = new StringUtils.SBuilder();
        // 解析 <resultMap></resultMap>
        sb.build("    <resultMap id=\"", ID, "\" type=\"", TYPE, "\">\n");

        // 处理 ID_MAP, 可能存在 [联合主键]
        ID_MAP.forEach((property, column) -> {
            sb
                    .build("        <!-- 主键索引, 建议使用这个字段作为条件进行查询 -->\n")
                    .build("        <id property=\"", property, "\" column=\"", column, "\"/>\n");
        });


        // 处理 ID_MAP, 可能存在 [联合主键]
        ONLY_MAP.forEach((property, column) -> {
            sb
                    .build("        <!-- 唯一索引, 建议使用这个字段作为条件进行查询 -->\n")
                    .build("        <result property=\"", property, "\" column=\"", column, "\"/>\n");
        });

        KEY_MAP.forEach((property, column) -> {
            sb
                    .build("        <!-- 普通索引, 建议使用这个字段作为条件进行查询 -->\n")
                    .build("        <result property=\"", property, "\" column=\"", column, "\"/>\n");
        });

        RESULT_MAP.forEach((property, column) -> {
            sb
                    .build("        <result property=\"", property, "\" column=\"", column, "\"/>\n");
        });

        ASSOCIATION_MAP.forEach((property, resultMap) -> {
            sb
                    .build("        <association property=\"", property, "\" resultMap=\"", resultMap, "\"/>\n");
        });

        COLLECTION_MAP.forEach((property, resultMap) -> {
            sb
                    .build("        <collection property=\"", property, "\" resultMap=\"", resultMap, "\"/>\n");
        });

        sb.build("    </resultMap>\n");

        return sb.toString();
    }


    /**
     * 生成sql片段
     *
     * @param id      id
     * @param context 内容
     * @return 返回一个 <sql>...</sql> 文本化的 XML
     */
    private String generateSQLFragment(String id, String context) {
        StringUtils.SBuilder sb = new StringUtils.SBuilder();
        // 定义 <sql> 片段
        sb.build("    <sql id=\"", id, "\">", context, "</sql>\n\n");
        return sb.toString();
    }

    /**
     * 生成 INSERT 的 mappedStatement
     */
    private String generateInsert(String annotationText, String id, String type, String insertSQL) {
        StringUtils.SBuilder sb = new StringUtils.SBuilder();
        if (!StringUtils.isEmpty(annotationText)) {
            sb.build("    <!-- ", annotationText, " -->\n");
        }
        sb.build("    <insert id=\"", id, "\" parameterType=\"", type, "\" >\n", insertSQL, "\n    </insert>\n\n");
        return sb.toString();
    }

    /**
     * 生成 select 类型的 mappedStatement
     *
     * @param annotationText 注释文本
     * @param id             语句名称
     * @param resultMap      返回类型
     * @param parameterType  参数类型
     * @param selectSQL      查询语句
     * @return 返回 <select>...</select>
     */
    private String generateSelect(String annotationText, String id, String resultMap, String parameterType, String selectSQL) {
        StringUtils.SBuilder sb = new StringUtils.SBuilder();
        if (!StringUtils.isEmpty(annotationText)) {
            sb.build("    <!--", annotationText, "-->\n");
        }
        sb
                .build("    <select id=\"", id, "\" resultMap=\"", resultMap, "\" parameterType=\"", parameterType, "\">\n")
                .build("        ", selectSQL, "\n")
                .build("    </select>\n\n");
        return sb.toString();
    }

    /**
     * 生成 update 类型的 mappedStatement
     *
     * @param annotationText 注释文本
     * @param id             语句名称
     * @param parameterType  参数类型
     * @param updateSQL      更新语句
     * @return 返回 <update>...</update>
     */
    private String generateUpdate(String annotationText, String id, String parameterType, String updateSQL) {
        StringUtils.SBuilder sb = new StringUtils.SBuilder();
        if (!StringUtils.isEmpty(annotationText)) {
            sb.build("    <!--", annotationText, "-->\n");
        }
        return sb.build("    <update id=\"", id, "\" parameterType=\"", parameterType, "\">\n")
                .build("        ", updateSQL, "\n")
                .build("    </select>\n\n").toString();
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
        String ID = StringUtils.hump(tableDetail.getName()) + "Map";
        String TYPE = "com.example.pojo." + tableDetail.getTableName();
        final Map<String, String> ID_MAP = Maps.newLinkedHashMap();
        final Map<String, String> ONLY_MAP = Maps.newLinkedHashMap();
        final Map<String, String> KEY_MAP = Maps.newLinkedHashMap();
        final Map<String, String> RESULT_MAP = Maps.newLinkedHashMap();
        final Map<String, String> ASSOCIATION_MAP = Maps.newLinkedHashMap();
        final Map<String, String> COLLECTION_MAP = Maps.newLinkedHashMap();

        // 开始遍历索引字段
        super.ID_INDEX.forEach(tableDetailVo -> ID_MAP.put(tableDetailVo.getParamName(), tableDetailVo.getFieldName()));
        super.ONLY_INDEX.forEach(tableDetailVo -> ONLY_MAP.put(tableDetailVo.getParamName(), tableDetailVo.getFieldName()));
        super.MNL_INDEX.forEach((k, v) -> v.forEach(tableDetailVo -> KEY_MAP.put(tableDetailVo.getParamName(), tableDetailVo.getFieldName())));
        super.BASIC.forEach(tableDetailVo -> RESULT_MAP.put(tableDetailVo.getParamName(), tableDetailVo.getFieldName()));

        // 开始连接正文
        sb
                .build("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .build("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n")
                .build("<mapper namespace=\"").build(namespace).build("\">\n")

                // 生成 <resultMap>...</resultMap> 标签
                .build(generateResultMap(ID, TYPE, ID_MAP, ONLY_MAP, KEY_MAP, RESULT_MAP, ASSOCIATION_MAP, COLLECTION_MAP));


        StringUtils.SBuilder sql = new StringUtils.SBuilder();
        for (int i = 0; i < tableDetailVoList.size(); i++) {
            sql.build(tableDetailVoList.get(i).getFieldName());
            if (i != tableDetailVoList.size() - 1) {
                sql.build(",");
            }
        }

        // 生成 <sql id='baseColumn'>...</sql>
        sb.build(generateSQLFragment("baseColumn", sql.toString()));


        StringUtils.SBuilder insertSb = StringUtils.builder("        INSERT INTO ", tableDetail.getName(), "\n");

        // 选择插入的 key
        StringUtils.SBuilder SELECT_INSERT_KEY = StringUtils.builder();

        // 选择插入的 val
        StringUtils.SBuilder SELECT_INSERT_VALUE = StringUtils.builder();

        SELECT_INSERT_KEY.build("        <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        // 循环遍历字段, 然后添加条件判断进去 names()
        tableDetailVoList.forEach(detail -> {
            SELECT_INSERT_KEY.build("            <if test=\"", detail.getParamName(), "!= null\">", detail.getFieldName(), ",</if>\n");
        });
        SELECT_INSERT_KEY.build("        </trim>\n");

        // values()
        SELECT_INSERT_VALUE.build("        <trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">\n");
        tableDetailVoList.forEach(detail -> {
            SELECT_INSERT_VALUE.build("            <if test=\"", detail.getParamName(), "!= null\">#{", detail.getFieldName(), "},</if>\n");
        });
        SELECT_INSERT_VALUE.build("        </trim>\n");

        // 生成 <insert id='insertSelective'> ... </insert>
        sb.build(generateInsert("根据传入的内容进行选择插入", "insertSelective", TYPE, insertSb.build(SELECT_INSERT_KEY.toString(), SELECT_INSERT_VALUE.toString()).toString()));

        // 存在 主键 的情况下使用
        if (super.ID_INDEX.size() > 0) {
            // 得到他的传入参数. 根据主键的个数确定. 如果超过1个.那么他就是 map, 否则就是字段对应的 java 类型
            String parameterType = "map";
            if (super.ID_INDEX.size() == 1) {
                parameterType = super.ID_INDEX.get(0).getFieldClassType().getName();
            }

            StringUtils.SBuilder expr = new StringUtils.SBuilder();
            for (int i = 0; i < super.ID_INDEX.size(); i++) {
                TableDetailVo v = super.ID_INDEX.get(i);
                expr.build(" ", v.getFieldName(), " = #{", v.getParamName(), "}");
                if (i != super.ID_INDEX.size() - 1) {
                    expr.build(" AND");
                }
            }

            StringUtils.SBuilder selectById = StringUtils.builder("SELECT <include = \"baseColumn\"/> FROM ", tableDetail.getName(), " WHERE ", expr.toString());
            sb.build(generateSelect("根据主键查询数据, 推荐使用这个方法查询数据", "selectById", ID, parameterType, selectById.toString()));

            StringUtils.SBuilder SELECT_UPDATE = StringUtils.builder("        UPDATE ", tableDetail.getName(), "\n")
                    .build("        <set>\n");
            // 根据部分字段进行修改的方法
            tableDetailVoList.forEach(detail -> SELECT_UPDATE.build("            <if test=\"", detail.getParamName(), " != null\">", detail.getFieldName(), " = #{", detail.getParamName(), "},</if>\n"));

            SELECT_UPDATE
                    .build("        </set>\n")
                    .build("        WHERE ", expr.toString());

            sb.build(generateUpdate("根据主键修改数据,建议使用此方法完成修改操作", "updateById", TYPE, SELECT_UPDATE.toString()));
        }

        // 处理唯一索引, 基于唯一索引生成 查询语句
        super.ONLY_INDEX.forEach(detailVo -> {
            // 基于唯一索引查询数据
            String selectId = "selectBy" + detailVo.getQueryParamName();
            String fieldParameterType = detailVo.getFieldClassType().getName();
            StringBuilder annotationBuilder = new StringBuilder("根据");
            String comment = detailVo.getComment();
            if (StringUtils.isEmpty(comment)) {
                comment = detailVo.getFieldName();
            }
            annotationBuilder.append(comment).append("查询数据, 推荐使用此方法查询数据. 因为它是基于唯一索引查询的");
            StringUtils.SBuilder selectSql = StringUtils.builder("SELECT <include = \"baseColumn\"/> FROM ", tableDetail.getName(), " WHERE ", detailVo.getFieldName(), " = #{", detailVo.getParamName() + "}");
            sb.build(generateSelect(annotationBuilder.toString(), selectId, ID, fieldParameterType, selectSql.toString()));


            // 基于唯一索引判断数据是否存在
            String containId = "containBy" + detailVo.getQueryParamName();
            sb.build(generateSelect("根据" + comment + "判断数据是否已经存在, 推荐使用此方法来确定数据唯一性. 因为他是基于唯一索引查询的", containId, "int", fieldParameterType, StringUtils.builder("SELECT COUNT(1) FROM ", tableDetail.getName(), " WHERE ", detailVo.getFieldName(), " = #{", detailVo.getParamName(), "}").toString()));
        });

        // 遍历普通索引 和 组合索引
        super.MNL_INDEX.forEach((keyName, keyList) -> {
            StringBuilder expr = new StringBuilder();
            StringBuilder annotationText = new StringBuilder("根据");
            StringBuilder mappedStatementID = new StringBuilder("selectBy");
            for (int i = 0; i < keyList.size(); i++) {
                TableDetailVo v = keyList.get(i);
                expr.append(" ").append(v.getFieldName()).append(" = #{").append(v.getParamName()).append("}");

                String comment = v.getComment();
                if (StringUtils.isEmpty(comment)) {
                    comment = v.getFieldName();
                }
                annotationText.append(comment);
                mappedStatementID.append(v.getQueryParamName());

                if (i != keyList.size() - 1) {
                    expr.append(" and");
                    annotationText.append("和");
                    mappedStatementID.append("And");
                }
            }

            String parameterType = "map";
            if (keyList.size() == 1) {
                parameterType = keyList.get(0).getFieldClassType().getName();
            }
            sb.build(generateSelect(annotationText.append("查询数据, 此方法虽然比不上主键和唯一索引, 但他也是走索引查询的").toString(), mappedStatementID.toString(), ID, parameterType, StringUtils.builder("SELECT <include = \"baseColumn\"/> FROM ", tableDetail.getName(), " WHERE ", expr.toString()).toString()));
        });

        sb.build("</mapper>\n");
        return sb.toString();
    }

}

