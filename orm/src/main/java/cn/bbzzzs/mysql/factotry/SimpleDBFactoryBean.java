package cn.bbzzzs.mysql.factotry;

import cn.bbzzzs.db.factory.DB;
import cn.bbzzzs.db.factory.DBFactory;
import cn.bbzzzs.db.result.bean.handler.DefaultResultXmlHandler;
import cn.bbzzzs.db.result.xml.ResultMapXMLReader;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class SimpleDBFactoryBean implements FactoryBean<DB> {

    @Autowired
    private DataSource dataSource;

//    private String result = "result.xml";

    @Override
    public DB getObject() throws Exception {
        DBFactory dbFactory = new DBFactory(dataSource.getConnection());

//        ResultMapXMLReader xmlReader = new ResultMapXMLReader(
//                dbFactory.getFactory(),
//                new ClassPathResource(result).getFile());
        return dbFactory.getDBInstance();
    }

    @Override
    public Class<?> getObjectType() {
        return DB.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
