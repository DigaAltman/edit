package cn.bbzzzs.mysql.handler.dao.support.pom.springjdbc;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.mysql.handler.dao.support.AbstractPomXml;

public class Pom extends AbstractPomXml {
    @Override
    protected void mapperDependencies(StringUtils.SBuilder sb) {
        sb.build("        <!-- spring-jdbc -->\n");
        sb.build("        <dependency>\n");
        sb.build("            <groupId>org.springframework.boot</groupId>\n");
        sb.build("            <artifactId>spring-boot-starter-jdbc</artifactId>\n");
        sb.build("        </dependency>\n");
    }
}
