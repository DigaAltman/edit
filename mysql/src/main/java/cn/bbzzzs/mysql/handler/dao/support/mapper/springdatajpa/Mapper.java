package cn.bbzzzs.mysql.handler.dao.support.mapper.springdatajpa;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.mysql.common.KeyEnum;
import cn.bbzzzs.mysql.handler.dao.support.AbstractMapper;
import cn.bbzzzs.mysql.pojo.TableDetail;
import cn.bbzzzs.mysql.service.TableService;
import cn.bbzzzs.mysql.vo.TableDetailVo;

import java.util.List;
import java.util.Map;

public class Mapper extends AbstractMapper {

    @Override
    protected Map mapperCode(StringUtils.SBuilder sb, TableDetail tableDetail, List<TableDetailVo> tableDetailVoList) {
        StringUtils.SBuilder appText = new StringUtils.SBuilder();
        appText.build("public interface CourseRepository extends JpaRepository<Integer, Course>,JpaSpecificationExecutor<Course> {\n");

        // 遍历唯一索引
        tableDetailVoList.stream().filter(dv -> !StringUtils.isEmpty(dv.getKey()) && KeyEnum.valueOf(dv.getKey()) == KeyEnum.UNI).forEach(oneKey -> {
            String paramName = StringUtils.hump(oneKey.getFieldName());
            String className = TableService.sqlTypeToJavaType(oneKey.getFieldType()).getSimpleName();

            // 生成查询方法
            appText
                    .build("    /**\n")
                    .build("     * @param ", paramName, " 根据", oneKey.getComment(), "查询数据,走唯一索引\n")
                    .build("     * @return 返回查询的一条数据\n")
                    .build("     */\n");
            appText.build("    Course findBy", StringUtils.humpFirstUpper(oneKey.getFieldName()),"(",className, " ", paramName,");\n\n");
        });

        appText.build("}\n");

        return null;
    }
}
