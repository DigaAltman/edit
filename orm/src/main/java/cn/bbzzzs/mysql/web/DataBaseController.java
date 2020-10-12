package cn.bbzzzs.mysql.web;

import cn.bbzzzs.mysql.pojo.DataBase;
import cn.bbzzzs.mysql.service.DataBaseService;
import cn.bbzzzs.mysql.vo.DataBaseDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/db")
public class DataBaseController {

    @Autowired
    private DataBaseService dataBaseService;

    @PostMapping("/detail")
    public DataBaseDetail getDataBaseDetail(@RequestBody @Validated DataBase dataBase) {
        return dataBaseService.getDetail(dataBase);
    }

}
