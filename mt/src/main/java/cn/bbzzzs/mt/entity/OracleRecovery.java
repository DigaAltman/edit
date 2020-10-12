package cn.bbzzzs.mt.entity;

import cn.bbzzzs.common.util.LogUtils;
import lombok.Data;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OracleRecovery implements Serializable {
    // 程序部署本地路径
    private String path;
    private String host;
    private String database;
    private String port;
    private String username;
    private String password;

    // 得到 oracle-recovery\download\inbound 下的所有需要恢复的 dmp 文件
    public List<DmpFile> getHandleDmpFileList() {
        File file = new File(path + File.separator + "download" + File.separator + "inbound");
        if (!file.exists()) {
            throw new IllegalArgumentException(file.getPath() + " 目录不存在");
        }

        if (!file.isDirectory()) {
            throw new IllegalArgumentException(file.getPath() + " 不是一个目录");
        }

        LogUtils.info("开始获取 %s 目录下的所有需要处理的 .dmp 后缀文件", file.getPath());

        List<DmpFile> dmpFileList = Arrays.stream(file.listFiles()).filter(f -> f.getName().length() > 4 && "dmp".equals(f.getName().substring(f.getName().lastIndexOf(".") + 1))).map(f -> new DmpFile().setFile(f).buildID()).collect(Collectors.toList());
        if (dmpFileList.size() == 0) {
            throw new IllegalArgumentException("没有需要处理的dmp文件 ..");
        }
        return dmpFileList;
    }


    public File getConfigPath() {
        return new File(path + File.separator + "config");
    }
}
