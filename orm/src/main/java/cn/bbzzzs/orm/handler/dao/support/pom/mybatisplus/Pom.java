package cn.bbzzzs.orm.handler.dao.support.pom.mybatisplus;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.orm.handler.dao.support.AbstractPomXml;

public class Pom extends AbstractPomXml {
    @Override
    protected void mapperDependencies(StringUtils.SBuilder sb) {
        sb.build("        <!-- 配置 mybatis-plus -->\n");
        sb.build("        <dependency>\n");
        sb.build("            <groupId>com.baomidou</groupId>\n");
        sb.build("            <artifactId>mybatis-plus-boot-starter</artifactId>\n");
        sb.build("            <version>3.1.0</version>\n");
        sb.build("        </dependency>\n\n");
        sb.build("        <!-- 配置 mybatis-plus 分页插件 -->\n");
        sb.build("        <dependency>\n");
        sb.build("            <groupId>com.github.pagehelper</groupId>\n");
        sb.build("            <artifactId>pagehelper</artifactId>\n");
        sb.build("            <version>5.1.4</version>\n");
        sb.build("        </dependency>\n");
    }
}
