package com.yanwu.demo.tcp.setver.tcp.setver.swing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static com.yanwu.demo.tcp.setver.tcp.setver.swing.SwingUtil.*;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-14 9:51.
 * <p>
 * description:
 */
@Slf4j
@Component
public class MyServer implements ApplicationListener<ContextRefreshedEvent>, Runnable {
    static final String OPEN_ICON = "icon/open.png";
    static final String CLOSE_ICON = "icon/close.png";
    private static JPanel panel;
    static Boolean isConnection = Boolean.FALSE;
    static JTextField portText;
    static JTextField beSelectText;
    static JTextField connectionNumText;
    static JTextArea logArea;
    static JTextField messageText;
    static JLabel statusIcon;
    static String channelKey;
    static JList<String> connections;
    static Map<String, String> channelMap = new HashMap<>(16);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        SwingUtilities.invokeLater(new MyServer());
    }

    @Override
    public void run() {
        createAndShowGui();
    }

    private void createAndShowGui() {
        log.info("打开TCP-服务端窗口");
        JFrame frame = new JFrame("TCP-服务端");
        frame.setVisible(Boolean.TRUE);
        // ----- 设置窗体固定大小
        frame.setResizable(Boolean.FALSE);
        frame.setSize(800, 800);
        // ----- 设置窗体关闭方式
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // ===== 添加 IP地址 & 端口 输入框
        panel = new JPanel();
        createPanel();
        frame.add(panel);
    }

    /**
     * 添加 IP地址 & 端口 输入框
     */
    private void createPanel() {
        // ----- 添加Icon
        createStatusIcon();
        // ----- 端口
        createPortInput();
        // ----- 建立连接按钮
        createOpenButton();
        // ----- 断开连接按钮
        createCloseButton();
        // ----- 当前连接 && 连接总数
        createConnectionSize();
        // ----- 清空日志按钮
        createCleanLog();
        // ----- 添加连接池区域
        createConnectionArea();
        // ----- 添加日志输出区域
        createLogScrollPane();
        // ----- 报文发送区
        createMessageButton();
    }

    /**
     * 创建状态icon
     */
    private void createStatusIcon() {
        statusIcon = new JLabel();
        setStatusIcon(Boolean.FALSE);
        statusIcon.setBounds(20, 25, 14, 14);
        panel.add(statusIcon);
    }

    /**
     * 端口
     */
    private void createPortInput() {
        panel.setLayout(null);
        JLabel portLabel = new JLabel("PORT:");
        portLabel.setBounds(50, 20, 80, 25);
        panel.add(portLabel);
        portText = new JTextField(20);
        portText.setBounds(100, 20, 50, 25);
        portText.setText("6000");
        panel.add(portText);
    }

    /**
     * 开启链接
     */
    private void createOpenButton() {
        JButton openButton = new JButton("开启连接");
        openButton.setBounds(170, 20, 90, 25);
        openButton.addActionListener(new OpenConnectionActionListener());
        panel.add(openButton);
    }

    /**
     * 关闭连接
     */
    private void createCloseButton() {
        JButton closeButton = new JButton("关闭连接");
        closeButton.setBounds(280, 20, 90, 25);
        closeButton.addActionListener(new CloseConnectionActionListener());
        panel.add(closeButton);
    }

    /**
     * 当前连接 && 连接总数
     */
    private void createConnectionSize() {
        // ----- 当前连接
        JLabel beSelect = new JLabel("当前选中:");
        beSelect.setBounds(390, 20, 80, 25);
        panel.add(beSelect);
        beSelectText = new JTextField(20);
        beSelectText.setBounds(460, 20, 80, 25);
        beSelectText.setDisabledTextColor(Color.black);
        beSelectText.setEnabled(Boolean.FALSE);
        panel.add(beSelectText);
        // ----- 连接总数
        JLabel connectionNum = new JLabel("总连接数:");
        connectionNum.setBounds(560, 20, 80, 25);
        panel.add(connectionNum);
        connectionNumText = new JTextField(20);
        connectionNumText.setBounds(630, 20, 30, 25);
        connectionNumText.setDisabledTextColor(Color.black);
        connectionNumText.setEnabled(Boolean.FALSE);
        connectionNumText.setText("0");
        panel.add(connectionNumText);
    }

    /**
     * 清空日志按钮
     */
    private void createCleanLog() {
        JButton cleanLogButton = new JButton("清空日志");
        cleanLogButton.setBounds(670, 20, 90, 25);
        cleanLogButton.addActionListener(new CleanLogActionListener());
        panel.add(cleanLogButton);
    }

    /**
     * 设备连接区
     */
    private void createConnectionArea() {
        connections = new JList<>();
        connections.setBounds(20, 60, 90, 680);
        panel.add(connections);
        connections.setListData(getNames());
        connections.addListSelectionListener(new BySelectionListListener());
    }

    /**
     * 日志输出区
     */
    private void createLogScrollPane() {
        JScrollPane logScrollPane = new JScrollPane();
        logScrollPane.setBounds(130, 60, 630, 630);
        panel.add(logScrollPane);
        logArea = new JTextArea();
        logArea.setBounds(130, 60, 630, 630);
        logScrollPane.setViewportView(logArea);
    }

    /**
     * 创建报文输入框和发送报文按钮
     */
    private void createMessageButton() {
        // ----- 报文输入框
        messageText = new JTextField(20);
        messageText.setBounds(130, 711, 530, 25);
        panel.add(messageText);
        // ----- 发送报文按钮
        JButton sendButton = new JButton("发送");
        sendButton.setBounds(670, 711, 90, 25);
        sendButton.addActionListener(new SendMessageActionListener());
        panel.add(sendButton);
    }

}
