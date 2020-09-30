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
    protected TableDetailVo ID;

    // 唯一索引字段
    protected List<TableDetailVo> ONLY_INDEX = Lists.newArrayList();

    // 普通索引或联合索引字段
    protected Map<String, List<TableDetailVo>> MNL_INDEX = Maps.newLinkedHashMap();

    /**
     * 填充上面的属性
     */
    public void fullProperties(TableDetail tableDetail, List<TableDetailVo> tableDetailVoList) {
        List<TableDetailVo> keyDetails = tableDetailVoList.stream().filter(tableDetailVo -> !StringUtils.isEmpty(tableDetailVo.getKey())).collect(Collectors.toList());
        List<TableDetailVo> idDetails = keyDetails.stream().filter(tableDetailVo -> KeyEnum.valueOf(tableDetailVo.getKey()) == KeyEnum.PRI).collect(Collectors.toList());

        // 初始化主键索引
        if (idDetails.size() > 0) {
            ID = idDetails.get(0);
        }

        // 初始化唯一索引
        keyDetails.stream().forEach(tableDetailVo -> {
            if (KeyEnum.UNI == KeyEnum.valueOf(tableDetailVo.getKey())) {
                ONLY_INDEX.add(tableDetailVo);
            }
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
     * @param tableDetail       数据表
     * @param tableDetailVoList 数据表中的字段
     */
    protected abstract Map mapperCode(StringUtils.SBuilder sb, TableDetail tableDetail, List<TableDetailVo> tableDetailVoList);
}
