package cn.bbzzzs.mysql.handler.dao.support.support;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.mysql.handler.dao.support.PomXmlHandler;

public class SpringDataJpaPomXmlHandler extends PomXmlHandler {

    @Override
    protected void mapperDependencies(StringUtils.SBuilder sb) {
        sb.build("        <!-- spring-data-jpa -->\n");
        sb.build("        <dependency>\n");
        sb.build("            <groupId>org.springframework.boot</groupId>\n");
        sb.build("            <artifactId>spring-boot-starter-data-jpa</artifactId>\n");
        sb.build("        </dependency>\n");
    }

}
