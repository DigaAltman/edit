package cn.bbzzzs.orm.handler.dao.support.mapper.springjdbc;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.orm.handler.dao.support.AbstractMapper;
import cn.bbzzzs.orm.pojo.TableDetail;
import cn.bbzzzs.orm.vo.TableDetailVo;

import java.util.List;
import java.util.Map;

public class Mapper extends AbstractMapper {
    @Override
    protected Map mapperCode(StringUtils.SBuilder sb, TableDetail tableDetail, List<TableDetailVo> tableDetailVoList) {
        return null;
    }
}
