package com.yanwu.demo.tcp.client.tcpclient.swing;

import com.yanwu.demo.tcp.client.tcpclient.handler.Handler;
import com.yanwu.demo.tcp.client.tcpclient.netty.NettyClient;
import com.yanwu.demo.tcp.client.tcpclient.utils.IpAndPortUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executor;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-08 16:35.
 * <p>
 * description:
 */
@Slf4j
@Component
public class SwingUtil implements ApplicationListener<ContextRefreshedEvent> {
    private static final String OPEN_ICON = "icon/open.png";
    private static final String CLOSE_ICON = "icon/close.png";
    private static Boolean isConnection = Boolean.FALSE;
    private static JTextField ipText;
    private static JTextField portText;
    private static JTextArea logArea;
    private static JTextField messageText;
    private static JLabel statusIcon;

    @Resource
    private NettyClient client;
    @Resource
    private Handler handler;
    @Resource
    private Executor nettyExecutor;

    public void createAndShowGUI() {
        log.info("打开TCP-客户端窗口");
        JFrame frame = new JFrame("TCP-客户端");
        frame.setVisible(Boolean.TRUE);
        // ----- 设置窗体大小
        frame.setSize(600, 450);
        // ----- 设置窗体关闭方式
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // ===== 添加 IP地址 & 端口 输入框
        JPanel panel = new JPanel();
        createPanel(panel);
        frame.add(panel);
    }

    /**
     * 添加 IP地址 & 端口 输入框
     *
     * @param panel
     */
    private void createPanel(JPanel panel) {
        // ----- IP地址
        createIPInput(panel);
        // ----- 端口
        createPortInput(panel);
        // ----- 建立连接按钮
        createEstablishButton(panel);
        // ----- 断开连接按钮
        createDisconnectButton(panel);
        // ----- 添加Icon
        createStatusIcon(panel);
        // ----- 日志输出框
        createLogScrollPane(panel);
        // ----- 清空日志按钮
        createCleanLogButton(panel);
        // ----- 报文输出框 &&　发送报文按钮
        createMessageButton(panel);
    }

    /**
     * 报文输入框
     *
     * @param panel
     */
    private void createMessageButton(JPanel panel) {
        // ----- 报文输入框
        messageText = new JTextField(20);
        messageText.setBounds(40, 350, 400, 25);
        panel.add(messageText);
        // ----- 发送报文按钮
        JButton sendButton = new JButton("发送");
        sendButton.setBounds(450, 350, 90, 25);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        panel.add(sendButton);
    }

    /**
     * 清空日志按钮
     *
     * @param panel
     */
    private void createCleanLogButton(JPanel panel) {
        JButton cleanLogButton = new JButton("清空日志");
        cleanLogButton.setBounds(450, 320, 90, 25);
        cleanLogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cleanLog();
            }
        });
        panel.add(cleanLogButton);
    }

    /**
     * 日志输出框
     *
     * @param panel
     */
    private void createLogScrollPane(JPanel panel) {
        JScrollPane logScrollPane = new JScrollPane();
        logScrollPane.setBounds(40, 60, 500, 250);
        panel.add(logScrollPane);
        logArea = new JTextArea();
        logArea.setBounds(40, 60, 500, 250);
        logScrollPane.setViewportView(logArea);
    }

    /**
     * 创建 断开连接 按钮
     *
     * @param panel
     */
    private void createDisconnectButton(JPanel panel) {
        JButton disconnectButton = new JButton("断开连接");
        disconnectButton.setBounds(430, 20, 90, 25);
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disconnectConnection();
            }
        });
        panel.add(disconnectButton);
    }

    /**
     * 添加连接状态icon
     *
     * @param panel
     */
    private void createStatusIcon(JPanel panel) {
        statusIcon = new JLabel();
        setStatusIcon(Boolean.FALSE);
        statusIcon.setBounds(540, 25, 14, 14);
        panel.add(statusIcon);
    }

    /**
     * 创建建立连接按钮
     *
     * @param panel
     */
    private void createEstablishButton(JPanel panel) {
        JButton establishButton = new JButton("建立连接");
        establishButton.setBounds(310, 20, 90, 25);
        establishButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                establishConnection();
            }
        });
        panel.add(establishButton);
    }

    /**
     * 创建端口输入框
     *
     * @param panel
     */
    private void createPortInput(JPanel panel) {
        panel.setLayout(null);
        JLabel portLabel = new JLabel("PORT:");
        portLabel.setBounds(190, 20, 80, 25);
        panel.add(portLabel);
        portText = new JTextField(20);
        portText.setBounds(230, 20, 40, 25);
        panel.add(portText);
    }

    /**
     * 创建IP地址输入框
     *
     * @param panel
     */
    private void createIPInput(JPanel panel) {
        panel.setLayout(null);
        JLabel ipLabel = new JLabel("IP:");
        ipLabel.setBounds(30, 20, 80, 25);
        panel.add(ipLabel);
        ipText = new JTextField(20);
        ipText.setBounds(50, 20, 110, 25);
        panel.add(ipText);
    }

    /**
     * 发送报文
     */
    private void sendMessage() {
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
            handler.send(message);
        } catch (Exception e) {
            printLog("发送失败, 请检查服务!", e);
        }
    }

    /**
     * 建立连接
     */
    private void establishConnection() {
        String ip = ipText.getText();
        String port = portText.getText();
        if (isConnection) {
            printLog("连接已建立, 请先断开连接!", null);
            return;
        }
        if (!IpAndPortUtil.checkIP(ip)) {
            printLog("建立连接失败, 请检查IP地址! ip: " + ip, null);
            return;
        }
        if (!IpAndPortUtil.checkPort(port)) {
            printLog("建立连接失败, 请检查端口! port: " + port, null);
            return;
        }
        nettyExecutor.execute(() -> {
            try {
                client.start(ip, Integer.valueOf(port));
                setStatus(Boolean.TRUE);
                setStatusIcon(Boolean.TRUE);
                printLog("建立连接! IP_PORT: " + ip + ":" + port, null);
            } catch (Exception e) {
                setStatus(Boolean.FALSE);
                setStatusIcon(Boolean.FALSE);
                printLog("建立连接失败, 请检查服务!", e);
            }
        });
    }

    /**
     * 断开连接
     */
    private void disconnectConnection() {
        if (!isConnection) {
            printLog("断开连接失败, 请先建立连接!", null);
            return;
        }
        handler.close();
        setStatus(Boolean.FALSE);
        setStatusIcon(Boolean.FALSE);
        printLog("断开连接成功!", null);
    }

    /**
     * 清空日志
     */
    private void cleanLog() {
        logArea.setText("");
    }

    /**
     * 输出日志
     *
     * @param message
     */
    public static void printLog(String message, Throwable e) {
        logArea.append(message + "\r\n");
        if (e == null) {
            log.info(message);
        } else {
            log.info(message, e);
        }
    }

    public static void setStatus(Boolean boo) {
        isConnection = boo;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        // ===== swing窗口
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private void setStatusIcon(Boolean flag) {
        String path;
        if (flag) {
            path = SwingUtil.class.getClassLoader().getResource(OPEN_ICON).getPath();
        } else {
            path = SwingUtil.class.getClassLoader().getResource(CLOSE_ICON).getPath();
        }
        statusIcon.setIcon(new ImageIcon(path));
    }
}
