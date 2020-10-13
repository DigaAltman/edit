package cn.bbzzzs.orm.handler.dao.support;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.orm.handler.dao.DaoHandler;
import cn.bbzzzs.orm.pojo.DataBase;
import cn.bbzzzs.orm.pojo.TableDetail;
import cn.bbzzzs.orm.vo.TableDetailVo;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 关于不同的 pom.xml 的抽象实现
 */
@Data
@Accessors(chain = true)
public abstract class AbstractPomXml implements DaoHandler {

    protected DaoHandler daoHandler;

    @Override
    public Map<String, List> handle(DataBase dataBase, TableDetail tableDetail, List<TableDetailVo> tableDetailVoList) {
        Map<String, List> res = new HashMap();
        StringUtils.SBuilder sb = new StringUtils.SBuilder();
        sb.build("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.build("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n");
        sb.build("    <modelVersion>4.0.0</modelVersion>\n\n");

        // TODO 用户配置部分
        sb.build("    <groupId>公司网站反写: www.imooc.com -> com.imooc</groupId>\n");
        sb.build("    <artifactId>项目名称: Edit -> edit </artifactId>\n");
        sb.build("    <version>历史版本: 0.0.1-SNAPSHOT(快照版本) / 0.0.1-RELEASE(正式版本)</version>\n");
        sb.build("    <packaging>jar / war</packaging>\n");
        sb.build("    <name>项目名称简介, 可以不写</name>\n");
        sb.build("    <description>项目名称简介, 可以不写</description>\n\n");

        // Spring Parent 部分
        sb.build("    <!-- 定义公共资源版本 -->\n");
        sb.build("    <parent>\n");
        sb.build("        <groupId>org.springframework.boot</groupId>\n");
        sb.build("        <artifactId>spring-boot-starter-parent</artifactId>\n");
        sb.build("        <version>2.0.0.RELEASE</version>\n");
        sb.build("        <relativePath/> <!-- lookup parent from repository -->\n");
        sb.build("    </parent>\n\n");

        // 版本配置部分
        sb.build("    <!-- 版本配置部分 -->\n");
        sb.build("    <properties>\n");
        sb.build("        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n");
        sb.build("        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>\n");
        sb.build("        <java.version>1.8</java.version>\n");
        sb.build("        <mysql.version>5.1.47</mysql.version>\n");
        sb.build("    </properties>\n\n");

        // 依赖配置部分， 针对 Jpa， Mybatis, Mybatis Plus, SpringJdbc, DB.依赖是不一样的
        sb.build("    <dependencies>\n\n");
        sb.build("        <!-- web模块 -->\n");
        sb.build("        <dependency>\n");
        sb.build("            <groupId>org.springframework.boot</groupId>\n");
        sb.build("            <artifactId>spring-boot-starter-web</artifactId>\n");
        sb.build("        </dependency>\n\n");
        sb.build("        <!-- lombok -->\n");
        sb.build("        <dependency>\n");
        sb.build("            <groupId>org.projectlombok</groupId>\n");
        sb.build("            <artifactId>lombok</artifactId>\n");
        sb.build("        </dependency>\n\n");
        sb.build("        <!-- 测试模块 -->\n");
        sb.build("        <dependency>\n");
        sb.build("            <groupId>org.springframework.boot</groupId>\n");
        sb.build("            <artifactId>spring-boot-starter-test</artifactId>\n");
        sb.build("            <scope>test</scope>\n");
        sb.build("        </dependency>\n\n");

        mapperDependencies(sb);

        sb.build("        <!--数据库相关-->\n");
        sb.build("        <dependency>\n");
        sb.build("            <groupId>mysql</groupId>\n");
        sb.build("            <artifactId>mysql-connector-java</artifactId>\n");
        sb.build("        </dependency>\n\n");
        sb.build("    </dependencies>\n\n");


        sb.build("    <!-- 打包部分 -->\n");
        sb.build("    <build>\n");
        sb.build("        <plugins>\n");
        sb.build("            <plugin>\n");
        sb.build("                <groupId>org.springframework.boot</groupId>\n");
        sb.build("                <artifactId>spring-boot-maven-plugin</artifactId>\n");
        sb.build("            </plugin>\n");
        sb.build("        </plugins>\n");
        sb.build("    </build>\n");
        sb.build("</project>");

        res.put("pom.xml", Lists.newArrayList(sb.toString()));

        Map<String, List> kvMap = daoHandler.handle(dataBase, tableDetail, tableDetailVoList);
        kvMap.forEach((k, v) -> res.put(k, v));
        return res;
    }

    /**
     * 获取具体的 mapper层 依赖信息
     *
     * @param sb
     * @return
     */
    protected abstract void mapperDependencies(StringUtils.SBuilder sb);

}
