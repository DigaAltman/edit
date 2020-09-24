package cn.bbzzzs.kafka.netty;

import cn.bbzzzs.common.util.LogUtils;
import cn.bbzzzs.kafka.http.MyHttpServletRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class BootStrapTest {
    public void open(int port) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup(5);

        bootstrap
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                // 添加处理器
                ch.pipeline().addLast("decode", new HttpRequestDecoder());
                ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
                ch.pipeline().addLast("servlet", new SimpleChannelInboundHandler() {
                    @Override
                    // 注意,这里我们的 请求会分为2次 打到这个处理器上,第一次是请求头, 第二次是请求体
                    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                        if (msg instanceof FullHttpRequest) {
                            FullHttpRequest req = (FullHttpRequest) msg;
                            LogUtils.debug("url: %s", req.uri());
                            LogUtils.debug(req.content().toString(Charset.defaultCharset()));

                            FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.OK);
                            resp.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=utf-8");
                            resp.content().writeBytes("{'status':200,'msg':'请求成功'}".getBytes());
                            ChannelFuture f = ctx.writeAndFlush(resp);
                            f.addListener(ChannelFutureListener.CLOSE);
                        }
                        else if (msg instanceof HttpRequest) {
                            HttpRequest req = (HttpRequest) msg;
                            LogUtils.debug("当前请求: %s", req.uri());
                        }
                        else if (msg instanceof HttpContent) {
                            ByteBuf content = ((HttpContent) msg).content();
                            File f = new File(this.getClass().getResource("").getFile() + "a.txt");
                            if (!f.getParentFile().exists()) {
                                f.getParentFile().mkdirs();
                            }
                            OutputStream o = new FileOutputStream(f);
                            content.readBytes(o, content.readableBytes());
                            o.close();
                        }
                        else if (msg instanceof LastHttpContent) {
                            FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.OK);
                            resp.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=utf-8");
                            resp.content().writeBytes("{'status':200, 'msg':'请求处理完毕!!'}".getBytes());
                            ChannelFuture f = ctx.writeAndFlush(resp);
                            f.addListener(ChannelFutureListener.CLOSE);
                        }
                    }
                });
                ch.pipeline().addLast("encode", new HttpResponseEncoder());
            }
        });

        ChannelFuture future = bootstrap.bind(port);
        future.addListener(f -> LogUtils.info("注册成功"));
    }

    public static void main(String[] args) throws IOException {
        BootStrapTest b = new BootStrapTest();
        b.open(8080);
        System.in.read();
    }

}
