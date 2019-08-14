package com.yanwu.demo.tcp.setver.tcp.setver.utils;

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
    /*** 端口取值范围 */
    private static final Integer[] PORT = {0, 65536};

    /**
     * 校验端口是否合法
     *
     * @param port 端口
     * @return 校验结果
     */
    public static Boolean checkPort(String port) {
        if (StringUtils.isEmpty(port)) {
            return Boolean.FALSE;
        }
        Integer p = Integer.valueOf(port);
        return p.compareTo(PORT[0]) > 0 && p.compareTo(PORT[1]) < 0;
    }
}
