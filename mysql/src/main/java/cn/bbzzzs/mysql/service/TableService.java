package cn.bbzzzs.mysql.service;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.db.factory.DB;
import cn.bbzzzs.db.factory.DBFactory;
import cn.bbzzzs.mysql.common.DaoEnum;
import cn.bbzzzs.mysql.factotry.ConnectionFactory;
import cn.bbzzzs.mysql.handler.dao.DaoHandler;
import cn.bbzzzs.mysql.handler.dao.support.applicationproperties.mybatis.Application;
import cn.bbzzzs.mysql.handler.dao.support.pom.mybatis.Pom;
import cn.bbzzzs.mysql.pojo.*;
import cn.bbzzzs.mysql.repositoy.TableDao;
import cn.bbzzzs.mysql.vo.TableDetailVo;
import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.*;

@Service
public class TableService {

    /**
     * Mapper层代码生成具体实现容器
     */
    private static final Map<DaoEnum, DaoHandler> daoEnumDaoHandlerMap = new HashMap();
    static {
        cn.bbzzzs.mysql.handler.dao.support.mapper.mybatis.Mapper mybatisMapper = new cn.bbzzzs.mysql.handler.dao.support.mapper.mybatis.Mapper();
        cn.bbzzzs.mysql.handler.dao.support.applicationproperties.mybatis.Application mybatisApplication = new cn.bbzzzs.mysql.handler.dao.support.applicationproperties.mybatis.Application();
        cn.bbzzzs.mysql.handler.dao.support.pom.mybatis.Pom mybatisPom = new cn.bbzzzs.mysql.handler.dao.support.pom.mybatis.Pom();
        mybatisPom.setDaoHandler(mybatisApplication);
        mybatisApplication.setDaoHandler(mybatisMapper);
        daoEnumDaoHandlerMap.put(DaoEnum.Mybatis, mybatisPom);

        cn.bbzzzs.mysql.handler.dao.support.mapper.springdatajpa.Mapper jpaRepository = new cn.bbzzzs.mysql.handler.dao.support.mapper.springdatajpa.Mapper();
        cn.bbzzzs.mysql.handler.dao.support.applicationproperties.springdatajpa.Application jpaApplication = new cn.bbzzzs.mysql.handler.dao.support.applicationproperties.springdatajpa.Application();
        cn.bbzzzs.mysql.handler.dao.support.pom.springdatajpa.Pom jpaPom = new cn.bbzzzs.mysql.handler.dao.support.pom.springdatajpa.Pom();
        jpaPom.setDaoHandler(jpaApplication);
        jpaApplication.setDaoHandler(jpaRepository);
        daoEnumDaoHandlerMap.put(DaoEnum.SpringDataJpa, jpaPom);

        cn.bbzzzs.mysql.handler.dao.support.mapper.mybatisplus.Mapper mybatisPlusMapper = new cn.bbzzzs.mysql.handler.dao.support.mapper.mybatisplus.Mapper();
        cn.bbzzzs.mysql.handler.dao.support.applicationproperties.mybatisplus.Application mybatisPlusApplication = new cn.bbzzzs.mysql.handler.dao.support.applicationproperties.mybatisplus.Application();
        cn.bbzzzs.mysql.handler.dao.support.pom.mybatisplus.Pom mybatisPlusPom = new cn.bbzzzs.mysql.handler.dao.support.pom.mybatisplus.Pom();
        mybatisPlusPom.setDaoHandler(mybatisPlusApplication);
        mybatisPlusApplication.setDaoHandler(mybatisPlusMapper);
        daoEnumDaoHandlerMap.put(DaoEnum.MybatisPlus, mybatisPlusPom);

        cn.bbzzzs.mysql.handler.dao.support.mapper.springjdbc.Mapper jdbcMapper = new cn.bbzzzs.mysql.handler.dao.support.mapper.springjdbc.Mapper();
        cn.bbzzzs.mysql.handler.dao.support.applicationproperties.springjdbc.Application jdbcApplication = new cn.bbzzzs.mysql.handler.dao.support.applicationproperties.springjdbc.Application();
        cn.bbzzzs.mysql.handler.dao.support.pom.springjdbc.Pom jdbcPom = new cn.bbzzzs.mysql.handler.dao.support.pom.springjdbc.Pom();
        jdbcPom.setDaoHandler(jdbcApplication);
        jdbcApplication.setDaoHandler(jdbcMapper);
        daoEnumDaoHandlerMap.put(DaoEnum.SpringJDBC, jdbcPom);
    }

    @Autowired
    private TableDao tableDao;

    /**
     * 获取数据库下的所有表名称
     */
    public List<String> getTable(DataBase dataBase) {
        Connection connection = ConnectionFactory.MYSQL.buildConnection(dataBase);
        DBFactory dbFactory = new DBFactory(connection);
        DB db = dbFactory.getDBInstance();

        return tableDao.getTableList(db);
    }

    /**
     * 获取数据库下的表的信息
     */
    public TableDetail getTable(DataBase dataBase, String name) {
        Connection connection = ConnectionFactory.MYSQL.buildConnection(dataBase);
        DBFactory dbFactory = new DBFactory(connection);
        DB db = dbFactory.getDBInstance();

        TableDetail tableDetail = tableDao.detail(db, name);
        return tableDetail;
    }

    /**
     * 获取数据表的字段和索引结构
     */
    public List<TableDetailVo> getStructure(DataBase dataBase, String name) {
        Connection connection = ConnectionFactory.MYSQL.buildConnection(dataBase);
        DBFactory dbFactory = new DBFactory(connection);
        DB db = dbFactory.getDBInstance();

        // 1. 拿到表结构
        List<TableStructure> tableStructureList = tableDao.getTableStructure(db, name);

        // 2. 拿到表的索引信息
        List<TableIndex> tableIndexList = tableDao.getTableIndex(db, name);

        // 3. 表字段注释
        List<TableFieldComment> tableFieldCommentList = tableDao.getTableFieldComment(db, name);

        // 3. 转换 pojo 为 vo
        List<TableDetailVo> tableDetailVoList = Lists.newLinkedList();
        for (TableStructure tableStructure : tableStructureList) {
            tableDetailVoList.add(TableDetailVo.ToVO(tableStructure, tableIndexList, tableFieldCommentList));
        }

        return tableDetailVoList;
    }

    /**
     * 基于表结构和字段结构生成实体类
     */
    public String generateEntity(DataBase dataBase, String name) {
        // 1. 获取表对应的结构信息
        TableDetail tableDetail = getTable(dataBase, name);
        if (tableDetail == null) {
            throw new IllegalArgumentException("数据库下指定表不存在");
        }

        // 2. 获取表结构信息
        List<TableDetailVo> detailVoList = getStructure(dataBase, name);

        StringUtils.SBuilder packageValue = StringUtils.builder();
        // TODO core1. 指定包路径
        packageValue.build("package cn.bbzzzs.edit.test.pojo;");

        // core2. 引入类依赖
        boolean lombok = false;
        // 判断 lombok 是否存在
        try {
            Class.forName("lombok.Data");
            lombok = true;
        } catch (Exception e) {
            lombok = false;
        }
        Set<Class> javaClassSet = new HashSet();
        StringUtils.SBuilder importValue = StringUtils.builder();


        String baseAnnotation = "/**\n" +
                " * @author edit\n" +
                " * @description ${comment}\n" +
                " */\n";

        StringUtils.SBuilder codeValue = StringUtils.builder();
        StringUtils.SBuilder tableComment = StringUtils.builder();
        if (!StringUtils.isEmpty(tableDetail.getComment())) {
            tableComment.build(tableDetail.getComment(), "表");
        }
        tableComment.build(" 存储引擎:", tableDetail.getEngine());
        tableComment.build(" 使用字符集:", tableDetail.getCollation());
        // core3. 创建类和类注释
        if (!StringUtils.isEmpty(tableComment.toString())) {
            String code = baseAnnotation.replace("${comment}", tableComment.toString());
            codeValue.build(code);
        }
        if (lombok) {
            javaClassSet.add(Data.class);
            codeValue.build("@Data\n");
        }
        codeValue.build("public class ", StringUtils.firstUpper(tableDetail.getName()), " implements java.io.Serializable {\n");

        String fieldAnnotation = "\t/**${comment}\n" +
                "\t */";

        // 循环遍历字段
        for (TableDetailVo detailVo : detailVoList) {
            StringUtils.SBuilder sb = StringUtils.builder();

            if (!StringUtils.isEmpty(detailVo.getKey())) {
                sb.build("\n\t * 索引类型: ", detailVo.getKey());
            }

            if (!StringUtils.isEmpty(detailVo.getIndexType())) {
                sb.build("\n\t * 使用: ", detailVo.getIndexType(), "结构");
            }

            if (!StringUtils.isEmpty(detailVo.getKeyName())) {
                sb.build("\n\t * 索引名称: ", detailVo.getKeyName());
            }

            if (detailVo.getSeqInIndex() != null) {
                sb.build("\n\t * 索引顺序: ", detailVo.getSeqInIndex().toString());
            }

            if (!StringUtils.isEmpty(detailVo.getIndexComment())) {
                sb.build("\n\t * 索引备注:", detailVo.getIndexComment());
            }

            if (!StringUtils.isEmpty(detailVo.getComment())) {
                sb.build("\n\t * 字段备注:", detailVo.getComment());
            }

            String fComment = sb.toString();
            if (!StringUtils.isEmpty(fComment)) {
                codeValue.build("\n", fieldAnnotation.replace("${comment}", fComment), "\n");
            }

            Class fieldClass = sqlTypeToJavaType(detailVo.getFieldType());
            javaClassSet.add(fieldClass);

            codeValue.build("\tprivate\t", fieldClass.getSimpleName(), "\t", StringUtils.hump(detailVo.getFieldName()));
            if (detailVo.getDefaultValue() != null && !Date.class.isAssignableFrom(fieldClass)) {
                codeValue.build(" = ");
                if (String.class.isAssignableFrom(fieldClass)) {
                    codeValue.build("\"", detailVo.getDefaultValue(), "\"");
                }
                else if (Number.class.isAssignableFrom(fieldClass)) {
                    codeValue.build(detailVo.getDefaultValue());
                }
            }
            codeValue.build(";\n");
        }

        codeValue.build("}");

        // 解决导入的类的问题
        for (Class importClass : javaClassSet) {
            if (!importClass.getName().startsWith("java.lang")) {
                importValue.build("import ", importClass.getName(), ";\n");
            }
        }


        return StringUtils.builder(packageValue.toString(), "\n\n", importValue.toString(), "\n", codeValue.toString(), "\n").toString();
    }

    // TODO 待实现, 持久化到 MySQL, 每个用户都有一个类型对应配置表. 可以配置sql对应的java类型
    public static Class sqlTypeToJavaType(String fieldType) {
        if (fieldType.startsWith("varchar")) {
            return String.class;
        } else if (fieldType.startsWith("char")) {
            return String.class;
        } else if (fieldType.startsWith("blob")) {
            return String.class;
        } else if (fieldType.startsWith("text")) {
            return String.class;
        } else if (fieldType.startsWith("int")) {
            return Integer.class;
        } else if (fieldType.startsWith("smallint")) {
            return Integer.class;
        } else if (fieldType.startsWith("bigint")) {
            return Long.class;
        } else if (fieldType.startsWith("float")) {
            return Float.class;
        } else if (fieldType.startsWith("double")) {
            return Double.class;
        } else if (fieldType.startsWith("decimal")) {
            return BigDecimal.class;
        } else if (fieldType.startsWith("tinyint")) {
            return Short.class;
        } else if (fieldType.startsWith("datetime")) {
            return Date.class;
        }
        return Object.class;
    }

    /**
     * 基于表结构生成 Repository/ Mapper / Dao 层
     * TODO 暂时还没有实现
     */
    public Map<String, List> generateRepository(DataBase dataBase, String name, String daoName) {
        DaoEnum daoEnum = DaoEnum.valueOf(daoName);

        // 拿到表信息
        TableDetail tableDetail = getTable(dataBase, name);

        // 拿到表的字段结构
        List<TableDetailVo> tableDetailVoList = getStructure(dataBase, name);

        // 拿到对应的处理器
        DaoHandler daoHandler = daoEnumDaoHandlerMap.get(daoEnum);

        Map<String, List> result = daoHandler.handle(dataBase, tableDetail, tableDetailVoList);

        return result;
    }


}
