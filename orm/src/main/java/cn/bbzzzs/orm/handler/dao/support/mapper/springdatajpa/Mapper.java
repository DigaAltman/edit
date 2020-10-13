package cn.bbzzzs.orm.handler.dao.support.mapper.springdatajpa;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.orm.handler.dao.support.AbstractMapper;
import cn.bbzzzs.orm.pojo.TableDetail;
import cn.bbzzzs.orm.vo.TableDetailVo;

import java.util.List;
import java.util.Map;

public class Mapper extends AbstractMapper {

    @Override
    protected Map mapperCode(StringUtils.SBuilder sb, TableDetail tableDetail, List<TableDetailVo> tableDetailVoList) {
        // 暂时不会做联合主键
        if (ID_INDEX.size() > 1) {
            throw new IllegalArgumentException("暂时不支持联合主键, 建议使用一个字段作为主键字段");
        }

        if (ID_INDEX.size() == 0) {
            throw new IllegalArgumentException("必须存在1个主键才能生成对应的持久类代码");
        }

        Class fieldClassType = ID_INDEX.get(0).getFieldClassType();
        needClassSet.add(fieldClassType);
        String idTypeName = fieldClassType.getSimpleName();

        StringUtils.SBuilder packageText = new StringUtils.SBuilder()
                .build("package ", MAPPER_PACKAGE_PATH, ";\n\n")
                .build("import ", ENTITY_PACKAGE_PATH, ".", ENTITY_NAME, ";\n")
                .build("import org.springframework.data.jpa.repository.JpaRepository;\n")
                .build("import org.springframework.data.jpa.repository.JpaSpecificationExecutor;\n\n");

        StringUtils.SBuilder appText = StringUtils.builder();

        appText.build("public interface ", MAPPER_NAME, " extends JpaRepository<", idTypeName, ",", ENTITY_NAME, ">, JpaSpecificationExecutor<", ENTITY_NAME, "> {\n");

        // 遍历 UNI
        ONLY_INDEX.forEach(oneKey -> {
            String paramName = oneKey.getParamName();
            Class fType = oneKey.getFieldClassType();
            needClassSet.add(fType);
            String className = fType.getSimpleName();


            // 生成查询方法
            appText
                    .build("    /**\n")
                    .build("     * @param ", paramName, " 根据", oneKey.getComment(), "查询数据,走唯一索引\n")
                    .build("     * @return 返回查询的一条数据\n")
                    .build("     */\n")
                    .build("    ", ENTITY_NAME, " findBy", StringUtils.humpFirstUpper(oneKey.getFieldName()), "(", className, " ", paramName, ");\n\n");

        });

        // 遍历 MUL
        MNL_INDEX.forEach((index, detailVoList) -> {
            StringUtils.SBuilder methodName = StringUtils.builder();
            StringUtils.SBuilder paramsName = StringUtils.builder();
            StringUtils.SBuilder annotation = StringUtils.builder("根据 ");
            StringUtils.SBuilder paramsText = StringUtils.builder();

            for (int i = 0; i < detailVoList.size(); i++) {
                TableDetailVo tableDetailVo = detailVoList.get(i);
                String comment = tableDetailVo.getComment();
                String paramName = tableDetailVo.getParamName();
                paramsText.build("@param ", paramName," ", comment,"\n");
                Class type = tableDetailVo.getFieldClassType();
                needClassSet.add(type);
                if (StringUtils.isEmpty(comment)) {
                    comment = paramName;
                }
                annotation.build(comment);
                methodName.build(StringUtils.firstUpper(tableDetailVo.getParamName()));
                paramsName.build(type.getSimpleName(), " ", paramName);
                if (i != detailVoList.size() - 1) {
                    methodName.build("And");
                    paramsName.build(",");
                    annotation.build("和");
                }
            }

            annotation.build(" 查询数据, 使用");
            if (detailVoList.size() > 0) {
                annotation.build("组合索引");
            } else {
                annotation.build("普通索引");
            }

            // TODO ${Entity} findByParamName1AndParamName2(Object paramName1, Object paramName2);
            appText
                    .build("    /**\n")
                    .build("    /* ", annotation.toString(), "\n")
                    .build("     * ", paramsText.toString(), "\n")
                    .build("     */\n ")
                    .build("    ", ENTITY_NAME, " findBy", methodName.toString(), "(", paramsName.toString(), ");\n");
        });

        appText.build("}\n");

        return null;
    }
}
