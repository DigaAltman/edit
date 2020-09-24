package cn.bbzzzs.kafka.echo;

import cn.bbzzzs.common.util.LogUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server {
    private static final int DEFAULT_PORT = 8080;
    int port;
    ServerSocketChannel channel;
    Selector selector;

    public Server(int port) {
        this.port = port;
    }

    public Server() {
        this(DEFAULT_PORT);
    }

    public void open() {
        try {
            channel = ServerSocketChannel.open();
            selector = Selector.open();
            channel.bind(new InetSocketAddress(port));
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_ACCEPT);
            LogUtils.warn("服务器[%d]初始化完毕...", port);
            dispatch(selector, channel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dispatch(Selector selector, ServerSocketChannel channel) throws IOException {
        while (true) {
            // 获取需要处理的数量
            int count = selector.select();

            // 没有需要处理的就放弃
            if (count == 0) {
                continue;
            }

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                // 取出需要处理的 SelectionKey, 然后从需要处理的队列中移除
                SelectionKey k = iterator.next();
                iterator.remove();
                // 请求无效直接跳过
                if (!k.isValid()) {
                    continue;
                }
                // 请求连接上来了
                else if (k.isAcceptable()) {
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    sc.register(selector, SelectionKey.OP_READ);
                }
                // 请求是可读的
                else if (k.isReadable()) {
                    SocketChannel sc = (SocketChannel) k.channel();
                    ByteBuffer bb = ByteBuffer.allocate(1024);
                    sc.read(bb);
                    if (bb.hasRemaining() && bb.get(0) == 4) {// 传输结束
                        channel.close();
                        LogUtils.error("关闭管道");
                        break;
                    }
                    // 处理返回结果
                    InetSocketAddress address = (InetSocketAddress) sc.getRemoteAddress();
                    String clientMessage = address.getHostName() + ":" + address.getPort();

                    String message = Main.handleMessage(bb);
                    LogUtils.info(" %s 能成功发送请求, 内容是: %s", clientMessage, message);

                    ByteBuffer byteBuffer = ByteBuffer.wrap("success!!".getBytes());
                    sc.write(byteBuffer);
                }
            }
        }
    }
}
