package cn.bbzzzs.orm.handler.dao.support;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.orm.handler.dao.DaoHandler;
import cn.bbzzzs.orm.pojo.DataBase;
import cn.bbzzzs.orm.pojo.TableDetail;
import cn.bbzzzs.orm.util.SQLUtil;
import cn.bbzzzs.orm.vo.TableDetailVo;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 关于不同的 application.properties 的抽象实现
 */
public abstract class AbstractApplicationProperties implements DaoHandler {

    @Setter
    private DaoHandler daoHandler;

    /**
     * 处理 application.properties 中的通用配置
     * @return
     */
    @Override
    public Map<String, List> handle(DataBase dataBase, TableDetail tableDetail, List<TableDetailVo> tableDetailVoList) {
        Map<String, List> resultMap = new HashMap();
        StringUtils.SBuilder sb = new StringUtils.SBuilder();
        sb.build("spring.datasource.url=", SQLUtil.buildUrl(dataBase), "\n");
        sb.build("spring.datasource.username=", dataBase.getUsername(), "\n");
        sb.build("spring.datasource.password=", dataBase.getPassword(), "\n");
        sb.build("spring.datasource.driver-class-name=com.mysql.jdbc.Driver\n");

        mapperApplication(sb);

        resultMap.put("application.properties", Arrays.asList(sb.toString()));
        Map<String, List> childDaoResult = daoHandler.handle(dataBase, tableDetail, tableDetailVoList);
        childDaoResult.forEach((k,v) -> resultMap.put(k, v));
        return resultMap;
    }

    /**
     * 具体的 application.properties 针对当前持久层框架的配置
     *
     * @param sb
     * @return
     */
    protected abstract void mapperApplication(StringUtils.SBuilder sb);
}
