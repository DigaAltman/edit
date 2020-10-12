package cn.bbzzzs.common.util;


import com.google.common.collect.Lists;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.List;
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
    @Deprecated
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
     * 推荐使用, 基于 NIO 的方式读取文件内容
     *
     * @param file     读取的文件
     * @param encode   字符集
     * @param consumer 每行内容执行的函数
     */
    public static int readLineByNIOSetEncode(File file, String encode, Consumer<String> consumer) {
        // 将文件转换位 Channel
        FileChannel channel = null;

        // 存放读取的每行数据
        List<String> dataList = Lists.newArrayList();

        // 一次读取 8kb
        ByteBuffer byteBuffer = ByteBuffer.allocate(8 * 1024);

        // temp：由于是按固定字节读取，在一次读取中，第一行和最后一行经常是不完整的行，因此定义此变量来存储上次的最后一行和这次的第一行的内容，
        // 并将之连接成完成的一行，否则会出现汉字被拆分成2个字节，并被提前转换成字符串而乱码的问题
        byte[] bytes = new byte[0];

        try {
            channel = new FileInputStream(file).getChannel();
            // 循环读取数据到缓冲区
            while (channel.read(byteBuffer) != -1) {
                // 读取结束后的位置，相当于读取的长度
                int readLength = byteBuffer.position();
                // 用来存放读取的内容的数组
                byte[] bs = new byte[readLength];

                // 读取数据到 bs 数组中
                byteBuffer.rewind();
                byteBuffer.get(bs);
                byteBuffer.clear();

                int startNum = 0;
                // 换行符, 回车符
                int LF = 10, CR = 13;
                // 是否有换行符
                boolean hasLF = false;

                // 解析 bs 这个数组, 判断里面是否包含换行符
                for (int i = 0; i < readLength; i++) {
                    if (bs[i] == LF) {
                        hasLF = true;
                        int tempNum = bytes.length;
                        int lineNum = i - startNum;
                        // 数组大小已经去掉换行符
                        byte[] lineByte = new byte[tempNum + lineNum];

                        // 填充了lineByte[0]~lineByte[tempNum-1]
                        System.arraycopy(bytes, 0, lineByte, 0, tempNum);

                        bytes = new byte[0];
                        // 填充lineByte[tempNum]~lineByte[tempNum+lineNum-1]
                        System.arraycopy(bs, startNum, lineByte, tempNum, lineNum);

                        //一行完整的字符串(过滤了换行和回车)
                        String line = new String(lineByte, 0, lineByte.length, encode);
                        dataList.add(line);

                        //过滤回车符和换行符
                        if (i + 1 < readLength && bs[i + 1] == CR) {
                            startNum = i + 2;
                        } else {
                            startNum = i + 1;
                        }
                    }
                }

                if (hasLF) {
                    bytes = new byte[bs.length - startNum];
                    System.arraycopy(bs, startNum, bytes, 0, bytes.length);
                } else {
                    // 兼容单次读取的内容不足一行的情况
                    byte[] toTemp = new byte[bytes.length + bs.length];
                    System.arraycopy(bytes, 0, toTemp, 0, bytes.length);
                    System.arraycopy(bs, 0, toTemp, bytes.length, bs.length);
                    bytes = toTemp;
                }
            }

            // 兼容文件最后一行没有换行的情况
            if(bytes != null && bytes.length > 0){
                String line = new String(bytes, 0, bytes.length, encode);
                dataList.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        dataList.forEach(consumer::accept);
        return dataList.size();
    }

    private static ByteBuffer reAllocate(ByteBuffer stringBuffer) {
        final int capacity = stringBuffer.capacity();
        byte[] newBuffer = new byte[capacity * 2];
        System.arraycopy(stringBuffer.array(), 0, newBuffer, 0, capacity);
        return (ByteBuffer) ByteBuffer.wrap(newBuffer).position(capacity);
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
            file.deleteOnExit();
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

    public static int readLineByNIO(File file, Consumer<String> consumer) {
        return readLineByNIOSetEncode(file, "UTF-8", consumer);
    }

    /**
     * 读取文件的所有内容
     *
     * @param file
     * @return
     */
    public static String readFile(File file) {
        StringBuilder res = new StringBuilder();
        readLine(file, line -> res.append(line).append("\n"));
        return res.toString();
    }

    public static void main(String[] args) {
        FileUtils.readLine(new File("D:\\oracle-recovery\\download\\inbound\\2020-360781000.2020-09-01.dmp"), line -> {
            System.out.println(line);
        });
    }
}
