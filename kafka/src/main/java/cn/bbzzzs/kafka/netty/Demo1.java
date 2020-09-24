package cn.bbzzzs.kafka.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.junit.Test;

import java.net.InetSocketAddress;

public class Demo1 {
    EventLoopGroup loopGroup = new NioEventLoopGroup(1);

    @Test
    public void bindTest() {
        NioDatagramChannel channel = new NioDatagramChannel();
        loopGroup.register(channel);
        channel.bind(new InetSocketAddress(8080));
    }
}
