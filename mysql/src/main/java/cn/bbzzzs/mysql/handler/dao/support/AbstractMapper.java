package cn.bbzzzs.mysql.handler.dao.support;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.mysql.handler.dao.DaoHandler;
import cn.bbzzzs.mysql.pojo.DataBase;
import cn.bbzzzs.mysql.pojo.TableDetail;
import cn.bbzzzs.mysql.vo.TableDetailVo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public abstract class AbstractMapper implements DaoHandler {
    protected DaoHandler daoHandler;

    @Override
    public Map<String, List> handle(DataBase dataBase, TableDetail tableDetail, List<TableDetailVo> tableDetailVoList) {
        StringUtils.SBuilder sb = StringUtils.builder();
        Map<String, List> fileMap = new HashMap();

        Map codeMap = mapperCode(sb, tableDetail, tableDetailVoList);
        fileMap.put("code", Arrays.asList(codeMap));

        return fileMap;
    }

    /**
     * @param tableDetail       数据表
     * @param tableDetailVoList 数据表中的字段
     */
    protected abstract Map mapperCode(StringUtils.SBuilder sb, TableDetail tableDetail, List<TableDetailVo> tableDetailVoList);
}
