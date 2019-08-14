package com.yanwu.demo.tcp.setver.tcp.setver.swing;

import com.yanwu.demo.tcp.setver.tcp.setver.cache.ClientSessionMap;
import com.yanwu.demo.tcp.setver.tcp.setver.handler.Handler;
import com.yanwu.demo.tcp.setver.tcp.setver.netty.NettyServer;
import com.yanwu.demo.tcp.setver.tcp.setver.utils.IpAndPortUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import static com.yanwu.demo.tcp.setver.tcp.setver.swing.MyServer.*;

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
    private Handler handler;
    @Resource
    private NettyServer server;
    @Resource
    private Executor nettyExecutor;

    /**
     * 日志输出
     *
     * @param ctxId   通道号
     * @param message 报文
     */
    public static void businessProcess(String ctxId, String message) {
        String[] split = ctxId.split("-");
        String name = split[split.length - 1].toUpperCase();
        printLog(name + " >> 接收报文: " + message, null);
    }

    /**
     * 断开连接
     *
     * @param ctxId 通道号
     */
    public static void closeConnections(String ctxId) {
        String[] split = ctxId.split("-");
        String name = split[split.length - 1].toUpperCase();
        channelMap.remove(name);
        connections.setListData(getNames());
        printLog(name + " >> 断开连接: " + ctxId, null);
        setConnectionNum();
        if (name.equals(channelKey)) {
            beSelectText.setText("");
        }
    }

    /**
     * 建立连接
     *
     * @param ctxId 通道号
     */
    public static void appendConnections(String ctxId) {
        String[] split = ctxId.split("-");
        String name = split[split.length - 1].toUpperCase();
        channelMap.put(name, ctxId);
        connections.setListData(getNames());
        printLog(name + " >> 建立连接: " + ctxId, null);
        setConnectionNum();
    }

    public static void setConnectionNum() {
        connectionNumText.setText(String.valueOf(ClientSessionMap.size()));
    }

    /**
     * 发送报文
     */
    private static void sendMessage() {
        if (!isConnection) {
            printLog("发送失败, 请先建立连接!", new RuntimeException("发送失败, 请先建立连接!"));
            return;
        }
        String message = messageText.getText();
        if (StringUtils.isEmpty(message)) {
            printLog("发送失败, 请检查报文!", new RuntimeException("发送失败, 请检查报文!"));
            return;
        }
        String ctxId = channelMap.get(channelKey);
        if (StringUtils.isEmpty(ctxId)) {
            printLog("发送失败, 请选择正确连接!", new RuntimeException("发送失败, 请选择正确连接!"));
            return;
        }
        try {
            swingUtil.handler.sendMessage(ctxId, message);
            printLog(channelKey + " >> 发送报文: [" + message + "]", null);
        } catch (Exception e) {
            printLog("发送失败, 请检查服务!", e);
        }
    }

    /**
     * 建立连接
     */
    private static void openConnection() {
        String port = portText.getText();
        if (isConnection) {
            printLog("连接已打开, 请先关闭连接!", new RuntimeException("连接已打开, 请先关闭连接!"));
            return;
        }
        if (!IpAndPortUtil.checkPort(port)) {
            printLog("打开连接失败, 请检查端口! port: " + port, new RuntimeException("打开连接失败, 请检查端口!"));
            return;
        }
        swingUtil.nettyExecutor.execute(() -> {
            try {
                swingUtil.server.open(Integer.valueOf(port));
                isConnection = Boolean.TRUE;
                setStatusIcon(Boolean.TRUE);
                printLog("开启连接成功!", null);
            } catch (Exception e) {
                isConnection = Boolean.FALSE;
                setStatusIcon(Boolean.FALSE);
                printLog("打开连接失败, 请检查服务!", e);
            }
        });
    }

    /**
     * 断开连接
     */
    private static void closeConnection() {
        if (!isConnection) {
            printLog("关闭连接失败, 请先打开连接!", new RuntimeException("关闭连接失败, 请先打开连接!"));
            return;
        }
        swingUtil.server.close();
        isConnection = Boolean.FALSE;
        setStatusIcon(Boolean.FALSE);
        printLog("关闭连接成功!", null);
    }

    /**
     * 清空日志
     */
    private static void cleanLog() {
        logArea.setText("");
    }

    /**
     * 输出日志
     *
     * @param message 报文
     * @param e       异常信息
     */
    public static void printLog(String message, Throwable e) {
        logArea.append(LocalDateTime.now() + " " + message + "\r\n");
        if (e == null) {
            log.info(message);
        } else {
            log.error(message, e);
        }
    }

    static String[] getNames() {
        if (channelMap.size() <= 0) {
            return new String[]{};
        }
        List<String> names = new ArrayList<>(channelMap.keySet());
        String[] result = new String[names.size()];
        for (int i = 0; i < names.size(); i++) {
            result[i] = names.get(i);
        }
        return result;
    }

    static void setStatusIcon(Boolean flag) {
        statusIcon.setIcon(flag ? OPEN : CLOSE);
    }

    /**
     * 开启连接按钮事件
     */
    static class OpenConnectionActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            openConnection();
        }
    }

    /**
     * 断开连接按钮事件
     */
    static class CloseConnectionActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            closeConnection();
        }
    }

    /**
     * 清空日志按钮事件
     */
    static class CleanLogActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            SwingUtil.cleanLog();
        }
    }

    /**
     * 左侧列表选中事件
     */
    static class BySelectionListListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (connections.getValueIsAdjusting()) {
                // ----- 鼠标点击
                channelKey = connections.getSelectedValue();
                beSelectText.setText(channelKey);
                // listValueChanged();
            } else {
                // ----- 鼠标释放
                log.info("list change: {}", connections.getSelectedValue());
            }
        }
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
}
