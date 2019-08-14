package com.yanwu.demo.tcp.setver.tcp.setver.utils;

import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-09 13:10.
 * <p>
 * description:
 */
public final class NettyUtils {
    private static final String SPLIT_PORT = ":";

    private NettyUtils() {
    }

    public static InetSocketAddress getRemoteAddress(ChannelHandlerContext ctx) {
        return (InetSocketAddress) ctx.channel().remoteAddress();
    }

    public static String getChannelId(ChannelHandlerContext ctx) {
        return ctx == null ? "" : ctx.channel().id().asLongText();
    }
}
