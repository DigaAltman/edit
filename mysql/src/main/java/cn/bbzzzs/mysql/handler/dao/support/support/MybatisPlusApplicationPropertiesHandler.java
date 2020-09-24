package cn.bbzzzs.mysql.handler.dao.support.support;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.mysql.handler.dao.support.ApplicationPropertiesHandler;

public class MybatisPlusApplicationPropertiesHandler extends ApplicationPropertiesHandler {

    @Override
    protected void mapperApplication(StringUtils.SBuilder sb) {
        sb.build("# mybatis-plus 配置 mapper 文件路径\n");
        sb.build("mybatis-plus.mapper-locations=classpath:mapper/*.xml\n\n");

        sb.build("# mybatis-plus 配置 实体扫描，多个package用逗号或者分号分隔\n");
        sb.build("mybatis-plus.typeAliasesPackage=com.example.mybatis.pojo\n\n");

        sb.build("# mybatis-plus 配置 驼峰转换\n");
        sb.build("mybatis-plus.configuration.map-underscore-to-camel-case=true\n");
        sb.build("mybatis-plus.configuration.cache-enabled=false\n");
        sb.build("mybatis-plus.configuration.call-setters-on-nulls=true\n\n");

        sb.build("# mybatis-plus 配置 刷新 mapper 调试神器\n");
        sb.build("mybatis-plus.global-config.refresh=true\n");
        sb.build("mybatis-plus.global-config.banner=false\n\n");

        sb.build("# mybatis-plus 配置 数据库信息\n");
        sb.build("mybatis-plus.db-config.db-type=mysql\n\n");

        sb.build("# mybatis-plus 配置 主键类型 [ AUTO:\"数据库ID自增\", INPUT:\"用户输入ID\",ID_WORKER:\"全局唯一ID (数字类型唯一ID)\", UUID:\"全局唯一ID UUID\" ]\n");
        sb.build("mybatis-plus.db-config.id-type=UUID\n\n");

        sb.build("# mybatis-plus 配置 字段策略 [ IGNORED:\"忽略判断\",NOT_NULL:\"非 NULL 判断\"),NOT_EMPTY:\"非空判断\" ]\n");
        sb.build("mybatis-plus.db-config.field-strategy=not_empty\n");
        sb.build("mybatis-plus.db-config.capital-mode=true\n\n");

        sb.build("# mybatis-plus 配置 逻辑删除配置\n");
        sb.build("mybatis-plus.db-config.logic-delete-value=1\n");
        sb.build("mybatis-plus.db-config.logic-not-delete-value=0\n");
    }

}
