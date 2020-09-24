package cn.bbzzzs.kafka.http;


import cn.bbzzzs.common.util.LogUtils;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    /**
     * 管理Servlet容器
     */
    private static Set<Class<? extends HttpServlet>> servletClassList = new LinkedHashSet();

    private static final int DEFAULT_PORT = 8080;
    ServerSocketChannel channel;
    Selector selector;
    ExecutorService executorService = Executors.newFixedThreadPool(5);


    public Server() {
        this(DEFAULT_PORT);
    }

    public Server(int port) {
        try {
            channel = ServerSocketChannel.open();
            selector = Selector.open();
            channel.bind(new InetSocketAddress(port));
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void startServer() throws IOException {
        while (true) {
            dispatch();
        }
    }

    public static void register(Class<? extends HttpServlet> servletClass) {
        servletClassList.add(servletClass);
    }

    public void dispatch() throws IOException {
        int count = selector.select(200);
        if (count == 0) {
            return;
        }
        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
        while (iterator.hasNext()) {
            SelectionKey k = iterator.next();
            iterator.remove();
            if (k.isAcceptable()) {
                final ServerSocketChannel channel = (ServerSocketChannel) k.channel();
                SocketChannel accept = channel.accept();
                accept.configureBlocking(false);
                accept.register(selector, SelectionKey.OP_READ);

            } else if (k.isReadable()) {
                final SocketChannel channel = (SocketChannel) k.channel();
                ByteBuffer b = ByteBuffer.allocate(8 * 1024);
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                while (channel.read(b) > 0) {
                    b.flip();
                    out.write(b.array(), 0, b.limit());
                    b.clear();
                }
                // 没有内容了
                if (out.size() <= 0) {
                    channel.close();
                    continue;
                }

                // 解码操作
                executorService.submit(() -> {
                    HttpServletRequest request = decode(out.toByteArray());
                    HttpServletResponse response = new MyHttpServletResponse();
                    Servlet servlet = getServlet(request);
                    try {
                        servlet.service(request, response);
                        channel.write(ByteBuffer.wrap(encode()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    //编码Http 服务
    private byte[] encode() {
        StringBuilder builder = new StringBuilder(512);
        builder.append("HTTP/1.1 ")
                .append(200).append(Code.message(Code.HTTP_OK)).append("\r\n");


        builder.append("Content-Length: ").append("success".length()).append("\r\n")
                    .append("Content-Type: text/html\r\n");


        builder.append("\r\n").append("success!!");
        return builder.toString().getBytes();
    }

    private HttpServlet getServlet(HttpServletRequest request) {
        // TODO 根据请求地址返回指定的 Servlet
        return new DispatcherServlet();
    }


    private HttpServletRequest decode(byte[] bytes) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
        HttpServletRequest request = new MyHttpServletRequest(reader);
        return request;
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.startServer();
    }

}


@WebServlet("/")
class DispatcherServlet extends HttpServlet {
    static {
        Server.register(DispatcherServlet.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LogUtils.warn("处理 get 请求");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LogUtils.warn("处理 post 请求");
    }
}