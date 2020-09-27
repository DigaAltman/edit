package cn.bbzzzs.mysql.handler.dao.support;

import cn.bbzzzs.mysql.handler.dao.DaoHandler;
import cn.bbzzzs.mysql.pojo.DataBase;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public abstract class AbstractMapper implements DaoHandler {
    protected DaoHandler daoHandler;

    @Override
    public Map<String, List> handle(DataBase dataBase, String tableName) {

        return new HashMap();
    }
}
