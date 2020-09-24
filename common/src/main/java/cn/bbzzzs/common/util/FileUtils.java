package cn.bbzzzs.common.util;


import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class FileUtils {

    /**
     * 包分割符
     */
    public static final String PACKAGE_SPERACTOR = ".";

    /**
     * java文件后缀
     */
    public static final String SUFFIX = ".java";


    /**
     * 读取文件, 并处理每行的结果
     *
     * @param file     文件
     * @param consumer 处理函数
     */
    public static void readLine(File file, Consumer<String> consumer) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                consumer.accept(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 读取文件, 并处理每行的结果
     *
     * @param file     文件
     * @param consumer 处理函数
     */
    public static Integer readLine(File file, BiFunction<Integer, String, String> consumer) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            int i = 0;
            String line = null;
            while ((line = br.readLine()) != null) {
                i++;
                consumer.apply(i, line);
            }
            return i;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将文件内容写入到目标文件中
     *
     * @param body 文件内容
     * @param dest 目标文件
     */
    public static void persistence(String body, File dest) {
        File dir = new File(dest.getParent());
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            if (!dest.exists()) {
                dest.createNewFile();
            }

            FileChannel destChannel = new FileOutputStream(dest).getChannel();
            destChannel.write(ByteBuffer.wrap(body.getBytes()));
            destChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 拷贝文件,将源文件拷贝到目标文件中
     *
     * @param src  源文件
     * @param dest 目标文件
     */
    public static void copy(File src, File dest) {
        FileChannel srcChannel = null;
        FileChannel destChannel = null;
        try {
            srcChannel = new FileInputStream(src).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(srcChannel, 0, srcChannel.size());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (srcChannel != null) {
                try {
                    srcChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (destChannel != null) {
                try {
                    destChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 通过包名获取源代码的路径
     * <p>
     * 比如: com.example.entity -> 项目路径/src/main/java/com/example/entity/
     *
     * @return 返回源代码的所在路径
     */
    public static String getPackagePath(String packageName) {
        if (!StringUtils.hasLength(packageName)) {
            return getSourceCodePath();
        }

        // 如果是 . 结尾的话, 并且包名称的长度不为 1
        if (packageName.endsWith(PACKAGE_SPERACTOR)) {
            if (packageName.length() == 1) {
                return getSourceCodePath();
            }
            packageName = packageName.substring(0, packageName.length() - 1);
        }

        // com.example.entity -> com/example/entity
        packageName.replace(PACKAGE_SPERACTOR, File.separator);
        return getSourceCodePath() + packageName.replace(PACKAGE_SPERACTOR, File.separator);
    }


    /**
     * 获取当前项目的源代码路径
     *
     * @return
     */
    public static String getSourceCodePath() {
        // maven 项目固定格式: src/main/java
        return StringUtils.concat(System.getProperty("user.dir"), File.separator, "src", File.separator, "main", File.separator, "java", File.separator);
    }


    /**
     * 获取项目路径
     */
    public static String getProjectPath() {
        return System.getProperty("user.dir");
    }


    /**
     * 向指定文件中编写数据
     *
     * @param file  文件
     * @param data  数据
     * @param model 模式, true 为追加. false 为覆盖
     */
    public static void saveFile(File file, String data, boolean model) {
        if (!model) {
            if (file.exists()) {
                file.delete();
            }
        }

        BufferedWriter wr = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            wr.write(data, 0, data.length());
            wr.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                wr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取项目的内容信息
     *
     * @return
     */
    public static String getProjectValue() {
        XmlQuery jQuery = new XmlQuery(new File(FileUtils.getProjectPath() + File.separator + "pom.xml"));
        String packageValue = jQuery.children("groupId").get(0).text();
        String projectValue = jQuery.children("artifactId").get(0).text();
        return packageValue + FileUtils.PACKAGE_SPERACTOR + projectValue;
    }


}
