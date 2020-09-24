package cn.bbzzzs.kafka.echo;

import cn.bbzzzs.common.util.LogUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Client {
    SocketChannel channel;
    Selector selector;

    /**
     * @param localhost ip地址
     * @param port      端口号
     */
    public void connect(final String localhost, final int port) throws IOException, InterruptedException {
        channel = SocketChannel.open();
        selector = Selector.open();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_CONNECT);
        boolean connection = channel.connect(new InetSocketAddress(localhost, port));

        while (true) {
            selector.select(100);
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey k = iterator.next();
                iterator.remove();

                if (!k.isValid()) {
                    continue;
                }
                else if (k.isConnectable()) {
                    // 完成连接,此时服务器才能进入到可读判断
                    channel.finishConnect();

                    k.interestOps(SelectionKey.OP_WRITE);
                }
                else if (k.isWritable()) {
                    channel.write(ByteBuffer.wrap("服务器-心跳健康".getBytes()));
                    k.interestOps(SelectionKey.OP_READ);
                }
                else if(k.isReadable()) {
                    ByteBuffer bb = ByteBuffer.allocate(1024);
                    channel.read(bb);

                    String message = Main.handleMessage(bb);
                    LogUtils.info(message);

                    // 每隔5秒切换一下状态, 发送一次心跳
                    Thread.sleep(5000);
                    k.interestOps(SelectionKey.OP_WRITE);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.connect("127.0.0.1", 8080);
    }
}
