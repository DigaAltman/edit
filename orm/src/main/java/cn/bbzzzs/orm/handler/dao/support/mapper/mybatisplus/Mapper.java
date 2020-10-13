package cn.bbzzzs.orm.handler.dao.support.mapper.mybatisplus;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.orm.handler.dao.support.AbstractMapper;
import cn.bbzzzs.orm.pojo.TableDetail;
import cn.bbzzzs.orm.vo.TableDetailVo;

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


