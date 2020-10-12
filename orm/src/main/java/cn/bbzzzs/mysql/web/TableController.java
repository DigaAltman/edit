package cn.bbzzzs.mysql.web;

import cn.bbzzzs.mysql.pojo.DataBase;
import cn.bbzzzs.mysql.pojo.TableDetail;
import cn.bbzzzs.mysql.service.TableService;
import cn.bbzzzs.mysql.vo.TableDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/table")
public class TableController {

    @Autowired
    private TableService tableService;

    /**
     * 获取数据库下的所有表
     * @param dataBase
     * @return
     */
    @PostMapping
    public List<String> tables(@RequestBody DataBase dataBase) {
        return tableService.getTable(dataBase);
    }

    /**
     * 获取数据表的详情信息
     */
    @PostMapping("/{table}")
    public TableDetail table(@RequestBody DataBase dataBase, @PathVariable String table) {
        return tableService.getTable(dataBase, table);
    }

    /**
     * 获取数据表中的字段信息
     */
    @PostMapping("/field/{table}")
    public List<TableDetailVo> field(@RequestBody DataBase dataBase, @PathVariable String table) {
        return tableService.getStructure(dataBase, table);
    }

    /**
     * 获取生成的实体类代码
     */
    @PostMapping("/entity/{table}")
    public String getEntity(@RequestBody DataBase dataBase, @PathVariable String table) {
        return tableService.generateEntity(dataBase, table);
    }

    /**
     * 获取生成的持久层代码
     */
    @PostMapping("/mapper/{table}")
    public Map<String, List> getMapper(@RequestBody DataBase dataBase, @PathVariable String table, @RequestParam(value = "daoName", defaultValue = "Mybatis") String daoName) {
        // 默认为 Mybatis
        return tableService.generateRepository(dataBase, table, daoName);
    }

}
