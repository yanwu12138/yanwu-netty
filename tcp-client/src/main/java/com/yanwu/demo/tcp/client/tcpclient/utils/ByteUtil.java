package com.yanwu.demo.tcp.client.tcpclient.utils;

import java.util.Arrays;

/**
 * @author <a herf="mailto:188234565@qq.com">胡佳</a>
 * @version 1.0 Created on 2014-11-6 下午02:21:34
 */
public class ByteUtil {

    /**
     * 字符串转数组
     *
     * @param strIn
     * @return
     */
    public static byte[] strToHexBytes(String strIn) {
        strIn = strIn.replaceAll(" ", "").toUpperCase();
        byte[] arrB = strIn.getBytes();
        int iLen = arrB.length;
        // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i = i + 2) {
            String strTmp = new String(arrB, i, 2);
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }

    /**
     * 字节数组转换十六进制数组输出
     *
     * @return
     */
    public static String bytesToHexStrPrint(byte[] arr) {
        String[] cs = new String[arr.length];
        for (int i = 0; i < arr.length; i++) {
            String hex = Integer.toHexString(arr[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            cs[i] = "" + hex.toUpperCase();
        }
        return Arrays.toString(cs);
    }

}
