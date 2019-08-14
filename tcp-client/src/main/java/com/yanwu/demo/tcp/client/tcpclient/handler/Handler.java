package com.yanwu.demo.tcp.client.tcpclient.handler;

import com.yanwu.demo.tcp.client.tcpclient.swing.SwingUtil;
import com.yanwu.demo.tcp.client.tcpclient.utils.ByteUtil;
import com.yanwu.demo.tcp.client.tcpclient.utils.ChannelUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.Executor;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-09 12:00.
 * <p>
 * description:
 */
@Slf4j
@Component
public class Handler extends ChannelInboundHandlerAdapter {

    @Resource
    private Executor nettyExecutor;

    private static Handler handler;
    private static ChannelHandlerContext channel;

    @PostConstruct
    public void init() {
        handler = this;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        channel = ctx;
        String message = "AA FF 3A" + ChannelUtil.getChannelNo() + "FF BB";
        log.info("send message, channel: {}, message: {}", channel.channel().id().asLongText(), message);
        SwingUtil.printLog("登陆报文: [" + message + "]", null);
        byte[] bytes = ByteUtil.hexStr2ByteArr(message);
        channel.writeAndFlush(Unpooled.copiedBuffer(bytes));
    }

    /**
     * 建立连接
     *
     * @param ctx 通道号
     * @param msg 报文
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf result = (ByteBuf) msg;
        byte[] bytes = new byte[result.readableBytes()];
        result.readBytes(bytes);
        String message = ByteUtil.bytesToHexPrint(bytes);
        // ===== 处理上行业务
        handler.nettyExecutor.execute(() -> SwingUtil.printLog("读取报文: " + message, null));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    /**
     * 断开连接
     *
     * @param ctx 通道号
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        offLine(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("netty error：" + cause);
    }

    /**
     * 发送报文
     *
     * @param message 报文
     */
    public void send(String message) {
        if (channel == null || StringUtils.isEmpty(message)) {
            return;
        }
        log.info("send message, channel: {}, message: {}", channel.channel().id().asLongText(), message);
        byte[] bytes = ByteUtil.hexStr2ByteArr(message);
        SwingUtil.printLog("发送报文: [" + message + "]", null);
        channel.writeAndFlush(Unpooled.copiedBuffer(bytes));
    }

    private void offLine(ChannelHandlerContext ctx) {
        try {
            ctx.channel().close();
            ctx.close();
            // ===== 处理断线业务
        } catch (Exception e) {
            log.error("channel close error: ", e);
        }
    }

    public void close() {
        offLine(channel);
        channel = null;
    }

    public void heartbeat() {
        String message = "AA FF 51" + ChannelUtil.getChannelNo() + "FF BB";
        send(message);
    }

}