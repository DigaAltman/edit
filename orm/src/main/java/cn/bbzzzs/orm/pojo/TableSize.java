package cn.bbzzzs.orm.pojo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class TableSize implements Serializable {
    private String tableName;
    private BigDecimal dataSize;
    private BigDecimal indexSize;
}
