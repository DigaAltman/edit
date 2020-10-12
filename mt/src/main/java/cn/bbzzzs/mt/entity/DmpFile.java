package cn.bbzzzs.mt.entity;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * 需要恢复的 dmp 文件对应的实体类
 */
@Data
@Accessors(chain = true)
public class DmpFile implements Serializable {
    private String ID = UUID.randomUUID().toString().replace("-", "");

    // 默认目录 myKettle / 时间戳
    private String kettleDir = "myKettle/" + ID;

    // dmp文件
    private File file;

    // ktr列表
    private List<String> kettleFileList = Lists.newLinkedList();

    public String getKettleFileNames(List<String> tableList) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < kettleFileList.size(); i++) {
            String s = kettleFileList.get(i);
            if (tableList.contains(s)) {
                res.append(s).append(".ktr");
                if (i != kettleFileList.size() - 1) {
                    res.append(",");
                }
            }
        }
        return res.toString();
    }

    public DmpFile buildID() {
        String name = file.getName();
        int index = name.lastIndexOf(".");
        this.ID = name.substring(0, index);
        return this;
    }
}
