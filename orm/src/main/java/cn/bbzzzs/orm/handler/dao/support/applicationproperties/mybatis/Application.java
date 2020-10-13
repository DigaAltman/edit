package cn.bbzzzs.orm.handler.dao.support.applicationproperties.mybatis;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.orm.handler.dao.support.AbstractApplicationProperties;

public class Application extends AbstractApplicationProperties {

    @Override
    protected void mapperApplication(StringUtils.SBuilder sb) {
        sb.build("# mybatis 的 XML 文件配置\n");
        sb.build("mybatis.mapper-locations=classpath: mybatis/*.xml\n\n");
        sb.build("# mybatis 的 别名 扫描配置\n");
        // TODO 这里需要根据配置进行动态写出
        sb.build("mybatis.type-aliases-package=cn.bbzzzs.pojo\n\n");
        sb.build("# mybatis 的 分页 插件配置\n");
        sb.build("pagehelper.helperDialect=mysql\n");
        sb.build("pagehelper.reasonable=true\n");
        sb.build("pagehelper.supportMethodsArguments=true\n");
        sb.build("pagehelper.params=count=countSql\n");
        sb.build("pagehelper.returnPageInfo=check\n");
    }
}
