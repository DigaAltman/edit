package cn.bbzzzs.kafka.netty;

import cn.bbzzzs.common.util.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.SneakyThrows;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class NettyDemo1 {

    @Test
    @SneakyThrows
    public void demo1() {
        //  tcp
        // 1. 初始化：打开管道 注册 绑定
        NioEventLoopGroup boss = new NioEventLoopGroup(5);
        NioEventLoopGroup work = new NioEventLoopGroup(10);

        // 2. 处理Accept事件 -》注册新管道
        NioServerSocketChannel channel = new NioServerSocketChannel();
        boss.register(channel);
        // 提交任务到 EventLoop
        channel.bind(new InetSocketAddress(8080));

        // 3. 处理写读事件：读取数据、业务处理、回写数据
        channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                // accept
                System.out.println(msg);
                System.out.println("已建立新连接");
                handlerAccept(work, msg);
            }
        });

        System.in.read();
    }

    private void handlerAccept(NioEventLoopGroup work, Object msg) {
        NioSocketChannel channel= (NioSocketChannel) msg;
        EventLoop loop = work.next();
        loop.register(channel);
        channel.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                LogUtils.warn("当前服务线程: %s", Thread.currentThread().getName());
                System.out.println(msg.toString(Charset.defaultCharset()));
            }
        });
    }


    @Test
    @SneakyThrows
    public void test1() {
        // 1. 构建一个NioEventLoopGroup, 里面包含一个 NioEventLoop
        NioEventLoopGroup loopGroup = new NioEventLoopGroup(2);
        loopGroup.submit(() -> LogUtils.error("submit %s", Thread.currentThread().getId()));
        loopGroup.execute(() -> LogUtils.warn("execute %s", Thread.currentThread().getId()));

        // 加上一个耗时操作, 让前面的异步任务执行完毕
        Thread.sleep(1000);
    }


    @Test
    @SneakyThrows
    public void test2() {
        NioEventLoopGroup loopGroup = new NioEventLoopGroup(1);
        NioDatagramChannel channel = new NioDatagramChannel();
        loopGroup.register(channel);
        // 注意,这个绑定回在loopGroup中的NioEventLoop中执行,因为它是IO操作
        // 所以是异步执行的, 他会返回一个Future对象
        ChannelFuture future = channel.bind(new InetSocketAddress(8080));
        // 通过回调函数来确认绑定成功
        future.addListener(future1 -> {
            LogUtils.info("绑定成功");
        });
        Thread.sleep(3000);
    }


    @Test
    @SneakyThrows
    public void test3() {
        NioEventLoopGroup loopGroup = new NioEventLoopGroup(1);
        NioDatagramChannel channel = new NioDatagramChannel();
        loopGroup.register(channel);
        // 注意,这个绑定回在loopGroup中的NioEventLoop中执行,因为它是IO操作
        // 所以是异步执行的, 他会返回一个Future对象
        ChannelFuture future = channel.bind(new InetSocketAddress(8080));
        // 通过回调函数来确认绑定成功
        future.addListener(future1 -> {
            LogUtils.info("绑定成功");
        });
        // 每隔channel都有自己的pipeline对象,我们可以添加具体的handler
        channel.pipeline().addLast(new SimpleChannelInboundHandler<Object>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                System.out.println(msg.getClass().getName());
            }
        });
        System.in.read();
    }
}
