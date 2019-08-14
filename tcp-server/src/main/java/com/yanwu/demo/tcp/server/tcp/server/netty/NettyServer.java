package com.yanwu.demo.tcp.server.tcp.server.netty;

import com.yanwu.demo.tcp.server.tcp.server.handler.ChannelHandler;
import com.yanwu.demo.tcp.server.tcp.server.utils.IpAndPortUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.concurrent.Executor;

/**
 * @author XuBaofeng.
 * @date 2018/6/24.
 */
@Slf4j
@Component
public class NettyServer {
    /*** 创建bootstrap */
    private ServerBootstrap serverBootstrap = new ServerBootstrap();
    /*** BOSS */
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    /*** Worker */
    private EventLoopGroup workGroup = new NioEventLoopGroup();
    @Resource
    Executor nettyExecutor;

    public void open(Integer port) {
        nettyExecutor.execute(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    serverBootstrap.group(bossGroup, workGroup)
                            .channel(NioServerSocketChannel.class)
                            .handler(new LoggingHandler(LogLevel.INFO))
                            .option(ChannelOption.SO_BACKLOG, 1024)
                            .childOption(ChannelOption.SO_KEEPALIVE, true)
                            .childHandler(new ChannelHandler());
                    //绑定端口，同步等待成功
                    if (IpAndPortUtil.checkPort(String.valueOf(port))) {
                        ChannelFuture channel = serverBootstrap.bind(port).sync();
                        channel.channel().closeFuture().sync();
                    }
                    log.info("netty server start on port:{}", port);
                }
            } catch (Exception e) {
                log.error("netty server start error: ", e);
            } finally {
                bossGroup.shutdownGracefully();
                workGroup.shutdownGracefully();
            }
        });
    }

    /**
     * 关闭服务器方法
     */
    @PreDestroy
    public void close() {
        log.info("netty server close ing ...");
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
        log.info("netty server close end ...");
    }

}
