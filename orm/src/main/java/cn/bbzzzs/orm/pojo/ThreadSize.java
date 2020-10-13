package cn.bbzzzs.orm.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ThreadSize implements Serializable {
    private Integer cacheSize;
    private Integer connectSize;
    private Integer createdSize;
    private Integer runSize;
}
