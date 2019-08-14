package com.yanwu.demo.tcp.client.tcpclient.netty;

import com.yanwu.demo.tcp.client.tcpclient.handler.Handler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-08 18:53.
 * <p>
 * description:
 */
@Slf4j
@Component
public class NettyClient {

    @Autowired
    private Executor nettyExecutor;

    public void start(String host, Integer port) throws Exception {
        nettyExecutor.execute(() -> {
            Bootstrap bootstrap = new Bootstrap();
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            try {
                bootstrap.group(bossGroup)
                        .channel(NioSocketChannel.class)
                        .remoteAddress(new InetSocketAddress(host, port))
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline().addLast(new Handler());
                            }
                        });
                ChannelFuture channel = bootstrap.connect().sync();
                log.info("Server Client Listen IP: [" + host + ":" + port + "]");
                channel.channel().closeFuture().sync();
            } catch (Exception e) {
                throw new RuntimeException("netty client start error");
            } finally {
                bossGroup.shutdownGracefully();
            }
        });
    }

}
