package com.yanwu.demo.tcp.client.tcpclient.utils;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-08 17:32.
 * <p>
 * description:
 */
public class IpAndPortUtil {
    /*** IP地址 ***/
    private static final String IP_NUM = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";
    private static final String IP_REGEX = "^" + IP_NUM + "\\." + IP_NUM + "\\." + IP_NUM + "\\." + IP_NUM + "$";
    /*** 端口取值范围 */
    private static final Integer[] PORT = {0, 65536};

    /**
     * 校验IP地址是否合法
     *
     * @return
     */
    public static Boolean checkIP(String ip) {
        Pattern pattern = Pattern.compile(IP_REGEX);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    /**
     * 校验端口是否合法
     *
     * @return
     */
    public static Boolean checkPort(String port) {
        if (StringUtils.isEmpty(port)) {
            return Boolean.FALSE;
        }
        Integer p = Integer.valueOf(port);
        return p.compareTo(PORT[0]) > 0 && p.compareTo(PORT[1]) < 0;
    }
}
