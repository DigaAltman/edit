package cn.bbzzzs.mysql.handler.dao.support;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.mysql.common.KeyEnum;
import cn.bbzzzs.mysql.handler.dao.DaoHandler;
import cn.bbzzzs.mysql.pojo.DataBase;
import cn.bbzzzs.mysql.pojo.TableDetail;
import cn.bbzzzs.mysql.vo.TableDetailVo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.*;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public abstract class AbstractMapper implements DaoHandler {
    // 后续的后置处理器
    protected DaoHandler daoHandler;

    // 主键索引字段
    protected List<TableDetailVo> ID_INDEX = Lists.newLinkedList();

    // 唯一索引字段
    protected List<TableDetailVo> ONLY_INDEX = Lists.newArrayList();

    // 普通索引或联合索引字段
    protected Map<String, List<TableDetailVo>> MNL_INDEX = Maps.newLinkedHashMap();

    // 普通字段
    protected List<TableDetailVo> BASIC = Lists.newLinkedList();

    // mapper 包管理部分 | TODO 日后修改
    protected String MAPPER_PACKAGE_PATH = "com.example.mapper";

    // entity 包管理部分 | TODO 日后修改
    protected String ENTITY_PACKAGE_PATH = "com.example.pojo";

    protected Set<Class> needClassSet = new HashSet();

    // 实体类名称
    protected String ENTITY_NAME = "";

    // 持久层名称
    protected String MAPPER_NAME = "";

    /**
     * 填充上面的属性
     */
    public void fullProperties(TableDetail tableDetail, List<TableDetailVo> tableDetailVoList) {
        List<TableDetailVo> keyDetails = tableDetailVoList.stream().filter(tableDetailVo -> !StringUtils.isEmpty(tableDetailVo.getKey())).collect(Collectors.toList());
        tableDetailVoList.stream().filter(tableDetailVo -> StringUtils.isEmpty(tableDetailVo.getKey())).forEach(BASIC::add);
        List<TableDetailVo> idDetails = keyDetails.stream().filter(tableDetailVo -> KeyEnum.valueOf(tableDetailVo.getKey()) == KeyEnum.PRI).collect(Collectors.toList());
        ENTITY_NAME = StringUtils.humpFirstUpper(tableDetail.getTableName());
        MAPPER_NAME = ENTITY_NAME + "Mapper";

        // 初始化主键索引
        idDetails.stream().forEach(tableDetailVo -> {
            if (KeyEnum.PRI == KeyEnum.valueOf(tableDetailVo.getKey())) {
                ID_INDEX.add(tableDetailVo);
            }
        });

        ID_INDEX.forEach(id -> {
            keyDetails.remove(id);
        });

        // 初始化唯一索引
        keyDetails.stream().forEach(tableDetailVo -> {
            if (KeyEnum.UNI == KeyEnum.valueOf(tableDetailVo.getKey())) {
                ONLY_INDEX.add(tableDetailVo);
            }
        });

        ONLY_INDEX.forEach(one -> {
            keyDetails.remove(one);
        });

        // 初始化普通索引
        for (TableDetailVo tableDetailVo : keyDetails) {
            List<TableDetailVo> innerList = MNL_INDEX.get(tableDetailVo.getKeyName());

            // 做一个 null 判断
            if (innerList == null) {
                innerList = new LinkedList();
                MNL_INDEX.put(tableDetailVo.getKeyName(), innerList);
            }

            innerList.add(tableDetailVo);
        }
    }

    @Override
    public Map<String, List> handle(DataBase dataBase, TableDetail tableDetail, List<TableDetailVo> tableDetailVoList) {
        fullProperties(tableDetail, tableDetailVoList);

        StringUtils.SBuilder sb = StringUtils.builder();
        Map<String, List> fileMap = new HashMap();

        Map codeMap = mapperCode(sb, tableDetail, tableDetailVoList);
        fileMap.put("javaCode", Arrays.asList(codeMap));

        return fileMap;
    }

    /**
     * 由于抽象类只能解决 package 和 import. 所以后续的代码还是要交给子类实现
     *
     * @param tableDetail       数据表
     * @param tableDetailVoList 数据表中的字段
     */
    protected abstract Map mapperCode(StringUtils.SBuilder sb, TableDetail tableDetail, List<TableDetailVo> tableDetailVoList);
}
