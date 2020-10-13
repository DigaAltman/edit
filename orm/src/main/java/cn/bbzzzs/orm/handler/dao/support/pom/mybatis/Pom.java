package cn.bbzzzs.orm.handler.dao.support.pom.mybatis;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.orm.handler.dao.support.AbstractPomXml;

public class Pom extends AbstractPomXml {

    @Override
    protected void mapperDependencies(StringUtils.SBuilder sb) {
        sb.build("        <!-- mybatis -->\n");
        sb.build("        <dependency>\n");
        sb.build("            <groupId>org.mybatis.spring.boot</groupId>\n");
        sb.build("            <artifactId>mybatis-spring-boot-starter</artifactId>\n");
        sb.build("            <version>1.3.2</version>\n");
        sb.build("        </dependency>\n\n");
        sb.build("        <!-- mybatis 分页插件 -->\n");
        sb.build("        <dependency>\n");
        sb.build("            <groupId>com.github.pagehelper</groupId>\n");
        sb.build("            <artifactId>pagehelper-spring-boot-starter</artifactId>\n");
        sb.build("            <version>1.2.5</version>\n");
        sb.build("        </dependency>\n\n");
    }
}
