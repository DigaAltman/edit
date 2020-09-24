package cn.bbzzzs.mysql.handler.dao.support.support;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.mysql.handler.dao.support.ApplicationPropertiesHandler;

public class SpringDataJpaApplicationPropertiesHandler extends ApplicationPropertiesHandler {

    @Override
    protected void mapperApplication(StringUtils.SBuilder sb) {
        sb.build("# Spring Data Jpa 配置\n");
        sb.build("jpa.hibernate.ddl-auto=update");
    }
}
