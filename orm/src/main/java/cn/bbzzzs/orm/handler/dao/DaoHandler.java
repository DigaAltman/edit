package cn.bbzzzs.orm.handler.dao;

import cn.bbzzzs.orm.pojo.DataBase;
import cn.bbzzzs.orm.pojo.TableDetail;
import cn.bbzzzs.orm.vo.TableDetailVo;

import java.util.List;
import java.util.Map;

public interface DaoHandler {

    /**
     * 针对不同的DAO层的处理方案
     *
     * @param dataBase          数据库信息
     * @param tableDetail       表信息
     * @param tableDetailVoList 表字段信息
     * @return
     */
    Map<String, List> handle(DataBase dataBase, TableDetail tableDetail, List<TableDetailVo> tableDetailVoList);

}
