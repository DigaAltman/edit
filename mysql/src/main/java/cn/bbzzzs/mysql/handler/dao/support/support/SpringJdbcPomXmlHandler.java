package cn.bbzzzs.mysql.handler.dao.support.support;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.mysql.handler.dao.support.PomXmlHandler;

public class SpringJdbcPomXmlHandler extends PomXmlHandler {

    @Override
    protected void mapperDependencies(StringUtils.SBuilder sb) {
        sb.build("        <!-- spring-jdbc -->\n");
        sb.build("        <dependency>\n");
        sb.build("            <groupId>org.springframework.boot</groupId>\n");
        sb.build("            <artifactId>spring-boot-starter-jdbc</artifactId>\n");
        sb.build("        </dependency>\n");
    }
}
