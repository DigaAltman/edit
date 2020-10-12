package cn.bbzzzs.mysql.common;

/**
 * 持久层的常用写法
 */
public enum DaoEnum {
    /**
     * Mybatis
     */
    Mybatis,

    /**
     * SpringDataJpa
     */
    SpringDataJpa,

    /**
     * Mybatis Plus
     */
    MybatisPlus,

    /**
     * Spring JDBC
     */
    SpringJDBC,

    /**
     * DB
     */
    DB;
}
