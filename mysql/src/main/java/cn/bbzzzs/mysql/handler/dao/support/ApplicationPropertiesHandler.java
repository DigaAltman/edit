package cn.bbzzzs.mysql.handler.dao.support;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.mysql.handler.dao.DaoHandler;
import cn.bbzzzs.mysql.pojo.DataBase;
import cn.bbzzzs.mysql.util.SQLUtil;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解决了 application.properties 的配置
 */
public abstract class ApplicationPropertiesHandler implements DaoHandler {

    @Setter
    private DaoHandler daoHandler;

    @Override
    public Map<String, List> handle(DataBase dataBase, String tableName) {
        Map<String, List> resultMap = new HashMap();
        StringUtils.SBuilder sb = new StringUtils.SBuilder();
        sb.build("spring.datasource.url=", SQLUtil.buildUrl(dataBase), "\n");
        sb.build("spring.datasource.username=", dataBase.getUsername(), "\n");
        sb.build("spring.datasource.password=", dataBase.getPassword(), "\n");
        sb.build("spring.datasource.driver-class-name=com.mysql.jdbc.Driver\n");

        mapperApplication(sb);

        resultMap.put("application.properties", Arrays.asList(sb.toString()));
        Map<String, List> childDaoResult = daoHandler.handle(dataBase, tableName);
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
