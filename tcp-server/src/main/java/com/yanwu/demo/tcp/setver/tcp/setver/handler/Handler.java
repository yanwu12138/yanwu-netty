package com.yanwu.demo.tcp.setver.tcp.setver.handler;

import com.yanwu.demo.tcp.setver.tcp.setver.cache.ClientSessionMap;
import com.yanwu.demo.tcp.setver.tcp.setver.swing.SwingUtil;
import com.yanwu.demo.tcp.setver.tcp.setver.utils.ByteUtil;
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
 * @date 2019-08-13 15:39.
 * <p>
 * description:
 */
@Slf4j
@Component
public class Handler extends ChannelInboundHandlerAdapter {

    @Resource
    Executor nettyExecutor;

    private static Handler handler;

    @PostConstruct
    public void init() {
        handler = this;
    }

    /**
     * 建立连接
     *
     * @param ctx 通道号
     * @param msg 报文
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String ctxId = ctx.channel().id().asLongText();
        if (ClientSessionMap.get(ctxId) == null) {
            handler.nettyExecutor.execute(() -> {
                // ----- 建立连接
                SwingUtil.appendConnections(ctxId);
            });
        }
        ClientSessionMap.put(ctxId, ctx);
        // ===== 处理上行业务
        byte[] bytes = (byte[]) msg;
        String message = ByteUtil.bytesToHexPrint(bytes);
        handler.nettyExecutor.execute(() -> SwingUtil.businessProcess(ctxId, message));
    }

    /**
     * 断开连接
     *
     * @param ctx 通道号
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        try {
            String ctxId = ctx.channel().id().asLongText();
            if (ClientSessionMap.get(ctxId) == null) {
                return;
            }
            log.info("channel close connection, channel: {}", ctxId);
            ctx.channel().close();
            ctx.close();
            // ===== 处理断线业务
            ClientSessionMap.remove(ctxId);
            SwingUtil.closeConnections(ctxId);
        } catch (Exception e) {
            log.error("channel close error: ", e);
        }
    }

    public void sendMessage(String ctxId, String message) {
        ChannelHandlerContext channel = ClientSessionMap.get(ctxId);
        if (channel == null || StringUtils.isEmpty(message)) {
            return;
        }
        byte[] bytes = ByteUtil.hexStr2ByteArr(message);
        log.info("send message, channel: {}, message: {}", ctxId, message);
        channel.writeAndFlush(bytes);
    }

}
