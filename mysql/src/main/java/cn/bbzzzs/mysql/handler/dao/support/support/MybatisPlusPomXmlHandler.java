package cn.bbzzzs.mysql.handler.dao.support.support;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.mysql.handler.dao.support.PomXmlHandler;

public class MybatisPlusPomXmlHandler extends PomXmlHandler {

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
