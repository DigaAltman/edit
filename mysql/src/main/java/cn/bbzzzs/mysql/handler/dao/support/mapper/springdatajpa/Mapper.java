package cn.bbzzzs.mysql.handler.dao.support.mapper.springdatajpa;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.mysql.handler.dao.support.AbstractMapper;
import cn.bbzzzs.mysql.pojo.TableDetail;
import cn.bbzzzs.mysql.vo.TableDetailVo;

import java.util.List;
import java.util.Map;

public class Mapper extends AbstractMapper {

    @Override
    protected Map mapperCode(StringUtils.SBuilder sb, TableDetail tableDetail, List<TableDetailVo> tableDetailVoList) {
        StringUtils.SBuilder appText = new StringUtils.SBuilder();

        appText.build("public interface CourseRepository extends JpaRepository<Integer, Course>,JpaSpecificationExecutor<Course> {\n");

        appText.build("}\n");

        return null;
    }
}
