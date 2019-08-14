package com.yanwu.demo.tcp.client.tcpclient.swing;

import com.yanwu.demo.tcp.client.tcpclient.handler.Handler;
import com.yanwu.demo.tcp.client.tcpclient.netty.NettyClient;
import com.yanwu.demo.tcp.client.tcpclient.utils.IpAndPortUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDateTime;
import java.util.concurrent.Executor;

import static com.yanwu.demo.tcp.client.tcpclient.swing.MyClient.*;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-08 16:35.
 * <p>
 * description:
 */
@Slf4j
@Component
public class SwingUtil {
    private static final String OPEN_ICON = "./classes/icon/open.png";
    private static final String CLOSE_ICON = "./classes/icon/close.png";
    private static final ImageIcon OPEN = new ImageIcon(new File(OPEN_ICON).getAbsoluteFile().toString());
    private static final ImageIcon CLOSE = new ImageIcon(new File(CLOSE_ICON).getAbsoluteFile().toString());

    private static SwingUtil swingUtil;

    @PostConstruct
    public void init() {
        swingUtil = this;
    }

    @Resource
    private NettyClient client;
    @Resource
    private Handler handler;
    @Resource
    private Executor nettyExecutor;

    /**
     * 输出日志
     *
     * @param message 数据体
     * @param e       异常信息
     */
    public static void printLog(String message, Throwable e) {
        logArea.append(LocalDateTime.now() + " " + message + "\r\n");
        if (e == null) {
            log.info(message);
        } else {
            log.info(message, e);
        }
    }

    /**
     * 更改ICON状态
     *
     * @param flag 状态
     */
    static void setStatusIcon(Boolean flag) {
        statusIcon.setIcon(flag ? OPEN : CLOSE);
    }

    /**
     * 建立连接
     */
    static class EstablishActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            establishConnection();
        }
    }

    private static void establishConnection() {
        String ip = ipText.getText();
        String port = portText.getText();
        if (isConnection) {
            printLog("连接已建立, 请先断开连接!", null);
            return;
        }
        if (!IpAndPortUtil.checkIp(ip)) {
            printLog("建立连接失败, 请检查IP地址! ip: " + ip, null);
            return;
        }
        if (!IpAndPortUtil.checkPort(port)) {
            printLog("建立连接失败, 请检查端口! port: " + port, null);
            return;
        }
        swingUtil.nettyExecutor.execute(() -> {
            try {
                swingUtil.client.start(ip, Integer.valueOf(port));
                isConnection = Boolean.TRUE;
                setStatusIcon(Boolean.TRUE);
                printLog("建立连接! IP_PORT: " + ip + ":" + port, null);
            } catch (Exception e) {
                isConnection = Boolean.FALSE;
                setStatusIcon(Boolean.FALSE);
                printLog("建立连接失败, 请检查服务!", e);
            }
        });
    }

    /**
     * 断开连接
     */
    static class DisconnectActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            disconnectConnection();
        }
    }

    private static void disconnectConnection() {
        if (!isConnection) {
            printLog("断开连接失败, 请先建立连接!", null);
            return;
        }
        swingUtil.handler.close();
        isConnection = Boolean.FALSE;
        setStatusIcon(Boolean.FALSE);
        printLog("断开连接成功!", null);
    }

    /**
     * 清空日志
     */
    static class CleanLogActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            cleanLog();
        }
    }

    private static void cleanLog() {
        logArea.setText("");
    }

    /**
     * 发送报文
     */
    static class SendMessageActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            sendMessage();
        }
    }

    private static void sendMessage() {
        if (!isConnection) {
            printLog("发送失败, 请先建立连接!", null);
            return;
        }
        String message = messageText.getText();
        if (StringUtils.isEmpty(message)) {
            printLog("发送失败, 请检查报文!", null);
            return;
        }
        try {
            swingUtil.handler.send(message);
        } catch (Exception e) {
            printLog("发送失败, 请检查服务!", e);
        }
    }
}
