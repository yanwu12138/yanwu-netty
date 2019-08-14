package com.yanwu.demo.tcp.client.tcpclient.swing;

import com.yanwu.demo.tcp.client.tcpclient.netty.NettyClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.swing.*;

import static com.yanwu.demo.tcp.client.tcpclient.swing.SwingUtil.*;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-14 10:47.
 * <p>
 * description:
 */
@Slf4j
@Component
public class MyClient implements ApplicationListener<ContextRefreshedEvent>, Runnable {
    static Boolean isConnection = Boolean.FALSE;
    private static JPanel panel;
    static JTextField ipText;
    static JTextField portText;
    static JTextArea logArea;
    static JTextField messageText;
    static JLabel statusIcon;
    @Resource
    private NettyClient client;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        // ===== swing窗口
        SwingUtilities.invokeLater(new MyClient());
    }

    @Override
    public void run() {
        createAndShowGui();
    }

    private void createAndShowGui() {
        log.info("打开TCP-客户端窗口");
        JFrame frame = new JFrame("TCP-客户端");
        frame.setVisible(Boolean.TRUE);
        // ----- 设置窗体固定大小
        frame.setResizable(Boolean.FALSE);
        frame.setSize(600, 450);
        // ----- 设置窗体关闭方式
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // ===== 添加 IP地址 & 端口 输入框
        panel = new JPanel();
        createPanel();
        frame.add(panel);
    }

    /**
     * 添加窗口组件
     */
    private void createPanel() {
        // ----- IP地址
        createIpInput();
        // ----- 端口
        createPortInput();
        // ----- 建立连接按钮
        createEstablishButton();
        // ----- 断开连接按钮
        createDisconnectButton();
        // ----- 添加Icon
        createStatusIcon();
        // ----- 日志输出框
        createLogScrollPane();
        // ----- 清空日志按钮
        createCleanLogButton();
        // ----- 报文输出框 &&　发送报文按钮
        createMessageButton();
    }

    /**
     * 创建IP地址输入框
     */
    private void createIpInput() {
        panel.setLayout(null);
        JLabel ipLabel = new JLabel("IP:");
        ipLabel.setBounds(30, 20, 80, 25);
        panel.add(ipLabel);
        ipText = new JTextField(20);
        ipText.setBounds(50, 20, 110, 25);
        ipText.setText("127.0.0.1");
        panel.add(ipText);
    }

    /**
     * 创建端口输入框
     */
    private void createPortInput() {
        panel.setLayout(null);
        JLabel portLabel = new JLabel("PORT:");
        portLabel.setBounds(190, 20, 80, 25);
        panel.add(portLabel);
        portText = new JTextField(20);
        portText.setBounds(230, 20, 40, 25);
        portText.setText("6000");
        panel.add(portText);
    }

    /**
     * 创建建立连接按钮
     */
    private void createEstablishButton() {
        JButton establishButton = new JButton("建立连接");
        establishButton.setBounds(310, 20, 90, 25);
        establishButton.addActionListener(new EstablishActionListener());
        panel.add(establishButton);
    }

    /**
     * 创建 断开连接 按钮
     */
    private void createDisconnectButton() {
        JButton disconnectButton = new JButton("断开连接");
        disconnectButton.setBounds(430, 20, 90, 25);
        disconnectButton.addActionListener(new DisconnectActionListener());
        panel.add(disconnectButton);
    }

    /**
     * 添加连接状态icon
     */
    private void createStatusIcon() {
        statusIcon = new JLabel();
        setStatusIcon(Boolean.FALSE);
        statusIcon.setBounds(540, 25, 14, 14);
        panel.add(statusIcon);
    }

    /**
     * 日志输出框
     */
    private void createLogScrollPane() {
        JScrollPane logScrollPane = new JScrollPane();
        logScrollPane.setBounds(40, 60, 500, 250);
        panel.add(logScrollPane);
        logArea = new JTextArea();
        logArea.setBounds(40, 60, 500, 250);
        logScrollPane.setViewportView(logArea);
    }

    /**
     * 清空日志按钮
     */
    private void createCleanLogButton() {
        JButton cleanLogButton = new JButton("清空日志");
        cleanLogButton.setBounds(450, 320, 90, 25);
        cleanLogButton.addActionListener(new CleanLogActionListener());
        panel.add(cleanLogButton);
    }

    /**
     * 报文输入框
     */
    private void createMessageButton() {
        // ----- 报文输入框
        messageText = new JTextField(20);
        messageText.setBounds(40, 350, 400, 25);
        panel.add(messageText);
        // ----- 发送报文按钮
        JButton sendButton = new JButton("发送");
        sendButton.setBounds(450, 350, 90, 25);
        sendButton.addActionListener(new SendMessageActionListener());
        panel.add(sendButton);
    }
}
