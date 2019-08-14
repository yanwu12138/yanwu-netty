package com.yanwu.demo.tcp.client.tcpclient.utils;

import org.apache.commons.lang3.RandomUtils;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-14 11:58.
 * <p>
 * description:
 */
public class ChannelUtil {
    private static final char[] CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final Integer SIZE = 10;
    private static String channel_no;

    static {
        StringBuilder builder = new StringBuilder(" ");
        for (int i = 0; i < SIZE; i++) {
            char aChar = CHARS[RandomUtils.nextInt(0, 16)];
            builder.append(aChar);
            if (i % 2 != 0) {
                builder.append(" ");
            }
        }
        channel_no = builder.toString();
    }

    public static String getChannelNo() {
        return channel_no;
    }
}
