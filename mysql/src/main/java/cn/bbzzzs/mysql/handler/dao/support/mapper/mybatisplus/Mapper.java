package cn.bbzzzs.mysql.handler.dao.support.mapper.mybatisplus;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.mysql.handler.dao.support.AbstractMapper;
import cn.bbzzzs.mysql.pojo.TableDetail;
import cn.bbzzzs.mysql.repositoy.TableDao;
import cn.bbzzzs.mysql.vo.TableDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public class Mapper extends AbstractMapper {

    @Override
    protected Map mapperCode(StringUtils.SBuilder sb, TableDetail tableDetail, List<TableDetailVo> tableDetailVoList) {
        StringUtils.SBuilder appText = new StringUtils.SBuilder();
        appText
                .build("package com.example.mapper;\n\n")
                .build("public interface UserMapper extends BaseMapper<User> { \n");

        // 因为 MybatisPlus 提供了 CRUD 接口, 所以我们就直接基于 唯一索引和组合索引进行代码生成就好了


        appText
                .build("}\n");


        return null;
    }
}


