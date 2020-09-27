package cn.bbzzzs.mysql.handler.dao.support.applicationproperties.springdatajpa;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.mysql.handler.dao.support.AbstractApplicationProperties;

public class Application extends AbstractApplicationProperties {

    @Override
    protected void mapperApplication(StringUtils.SBuilder sb) {
        sb.build("# Spring Data Jpa 配置\n");
        sb.build("jpa.hibernate.ddl-auto=update");
    }
}
