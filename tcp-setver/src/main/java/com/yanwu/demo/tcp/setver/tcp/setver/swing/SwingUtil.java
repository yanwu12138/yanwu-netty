package com.yanwu.demo.tcp.setver.tcp.setver.swing;

import com.yanwu.demo.tcp.setver.tcp.setver.handler.Handler;
import com.yanwu.demo.tcp.setver.tcp.setver.netty.NettyServer;
import com.yanwu.demo.tcp.setver.tcp.setver.utils.IpAndPortUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private static JTextField portText;
    private static JTextField beSelectText;
    private static JTextField connectionNumText;
    private static JTextArea logArea;
    private static JTextField messageText;
    private static JLabel statusIcon;
    private static String channelKey;
    private static JList<String> connections;
    private static Map<String, String> channelMap = new HashMap<>(16);

    @Resource
    private Handler handler;
    @Resource
    private NettyServer server;
    @Resource
    private Executor nettyExecutor;

    private void createAndShowGui() {
        log.info("打开TCP-服务端窗口");
        JFrame frame = new JFrame("TCP-服务端");
        frame.setVisible(Boolean.TRUE);
        // ----- 设置窗体大小
        frame.setSize(800, 800);
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
        // ----- 添加Icon
        createStatusIcon(panel);
        // ----- 端口
        createPortInput(panel);
        // ----- 建立连接按钮
        createOpenButton(panel);
        // ----- 断开连接按钮
        createCloseButton(panel);
        // ----- 当前连接 && 连接总数
        createConnectionSize(panel);
        // ----- 清空日志按钮
        createCleanLog(panel);
        // ----- 添加连接池区域
        createConnectionArea(panel);
        // ----- 添加日志输出区域
        createLogScrollPane(panel);
        // ----- 报文发送区
        createMessageButton(panel);
    }

    /**
     * 日志输出
     *
     * @param ctxId
     * @param message
     */
    public static void businessProcess(String ctxId, String message) {
        String[] split = ctxId.split("-");
        String name = split[split.length - 1].toUpperCase();
        printLog(name + " >> 接收报文: " + message, Boolean.TRUE, null);
    }

    /**
     * 断开连接
     *
     * @param ctxId
     */
    public static void closeConnections(String ctxId) {
        String[] split = ctxId.split("-");
        String name = split[split.length - 1].toUpperCase();
        channelMap.remove(name);
        connections.setListData(getNames());
        printLog("断开连接: " + ctxId, Boolean.TRUE, null);
        connectionNumText.setText(String.valueOf(channelMap.size()));
        if (name.equals(channelKey)) {
            beSelectText.setText("");
        }
    }

    /**
     * 建立连接
     *
     * @param ctxId
     */
    public static void appendConnections(String ctxId) {
        String[] split = ctxId.split("-");
        String name = split[split.length - 1].toUpperCase();
        channelMap.put(name, ctxId);
        connections.setListData(getNames());
        printLog("建立连接: " + ctxId, Boolean.TRUE, null);
        connectionNumText.setText(String.valueOf(channelMap.size()));
    }

    /**
     * 清空日志按钮
     *
     * @param panel
     */
    private void createCleanLog(JPanel panel) {
        JButton cleanLogButton = new JButton("清空日志");
        cleanLogButton.setBounds(670, 20, 90, 25);
        cleanLogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cleanLog();
            }
        });
        panel.add(cleanLogButton);
    }

    private void createMessageButton(JPanel panel) {
        // ----- 报文输入框
        messageText = new JTextField(20);
        messageText.setBounds(130, 711, 530, 25);
        panel.add(messageText);
        // ----- 发送报文按钮
        JButton sendButton = new JButton("发送");
        sendButton.setBounds(670, 711, 90, 25);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        panel.add(sendButton);
    }

    /**
     * 日志输出区
     *
     * @param panel
     */
    private void createLogScrollPane(JPanel panel) {
        JScrollPane logScrollPane = new JScrollPane();
        logScrollPane.setBounds(130, 60, 630, 630);
        panel.add(logScrollPane);
        logArea = new JTextArea();
        logArea.setBounds(130, 60, 630, 630);
        logScrollPane.setViewportView(logArea);
    }

    /**
     * 设备连接区
     *
     * @param panel
     */
    private void createConnectionArea(JPanel panel) {
        connections = new JList<String>();
        connections.setBounds(20, 60, 90, 680);
        panel.add(connections);
        connections.setListData(getNames());
        connections.addListSelectionListener(new ListSelectionListener() {
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
        });
    }

    /**
     * 当前连接 && 连接总数
     *
     * @param panel
     */
    private void createConnectionSize(JPanel panel) {
        // ----- 当前连接
        JLabel beSelect = new JLabel("当前选中:");
        beSelect.setBounds(390, 20, 80, 25);
        panel.add(beSelect);
        beSelectText = new JTextField(20);
        beSelectText.setBounds(460, 20, 80, 25);
        panel.add(beSelectText);
        // ----- 连接总数
        JLabel connectionNum = new JLabel("总连接数:");
        connectionNum.setBounds(560, 20, 80, 25);
        panel.add(connectionNum);
        connectionNumText = new JTextField(20);
        connectionNumText.setBounds(630, 20, 30, 25);
        panel.add(connectionNumText);
    }

    /**
     * 关闭连接
     *
     * @param panel
     */
    private void createCloseButton(JPanel panel) {
        JButton closeButton = new JButton("关闭连接");
        closeButton.setBounds(280, 20, 90, 25);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeConnection();
            }
        });
        panel.add(closeButton);
    }

    /**
     * 开启链接
     *
     * @param panel
     */
    private void createOpenButton(JPanel panel) {
        JButton openButton = new JButton("开启连接");
        openButton.setBounds(170, 20, 90, 25);
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openConnection();
            }
        });
        panel.add(openButton);
    }

    /**
     * 端口
     *
     * @param panel
     */
    private void createPortInput(JPanel panel) {
        panel.setLayout(null);
        JLabel portLabel = new JLabel("PORT:");
        portLabel.setBounds(50, 20, 80, 25);
        panel.add(portLabel);
        portText = new JTextField(20);
        portText.setBounds(100, 20, 50, 25);
        panel.add(portText);
    }

    /**
     * 创建状态icon
     *
     * @param panel
     */
    private void createStatusIcon(JPanel panel) {
        statusIcon = new JLabel();
        setStatusIcon(Boolean.FALSE);
        statusIcon.setBounds(20, 25, 14, 14);
        panel.add(statusIcon);
    }

    /**
     * 发送报文
     */
    private void sendMessage() {
        if (!isConnection) {
            printLog("发送失败, 请先建立连接!", Boolean.FALSE, null);
            return;
        }
        String message = messageText.getText();
        if (StringUtils.isEmpty(message)) {
            printLog("发送失败, 请检查报文!", Boolean.FALSE, null);
            return;
        }
        String ctxId = channelMap.get(channelKey);
        if (StringUtils.isEmpty(ctxId)) {
            printLog("发送失败, 请选择正确连接!", Boolean.FALSE, null);
            return;
        }
        try {
            handler.sendMessage(ctxId, message);
            printLog(channelKey + " >> 发送报文: " + message, Boolean.TRUE, null);
        } catch (Exception e) {
            printLog("发送失败, 请检查服务!", Boolean.FALSE, e);
        }
    }

    /**
     * 建立连接
     */
    private void openConnection() {
        String port = portText.getText();
        if (isConnection) {
            printLog("连接已打开, 请先关闭连接!", Boolean.FALSE, null);
            return;
        }
        if (!IpAndPortUtil.checkPort(port)) {
            printLog("打开连接失败, 请检查端口! port: " + port, Boolean.FALSE, null);
            return;
        }
        nettyExecutor.execute(() -> {
            try {
                server.open(Integer.valueOf(port));
                isConnection = Boolean.TRUE;
                setStatusIcon(Boolean.TRUE);
                printLog("开启连接成功!", Boolean.TRUE, null);
            } catch (Exception e) {
                isConnection = Boolean.FALSE;
                setStatusIcon(Boolean.FALSE);
                printLog("打开连接失败, 请检查服务!", Boolean.FALSE, e);
            }
        });
    }

    /**
     * 断开连接
     */
    private void closeConnection() {
        if (!isConnection) {
            printLog("关闭连接失败, 请先打开连接!", Boolean.FALSE, null);
            return;
        }
        server.close();
        isConnection = Boolean.FALSE;
        setStatusIcon(Boolean.FALSE);
        printLog("关闭连接成功!", Boolean.TRUE, null);
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
    private static void printLog(String message, Boolean flag, Throwable e) {
        logArea.append(message + "\r\n");
        if (flag) {
            log.info(message);
        } else {
            log.error(message, e);
        }
    }

    private static String[] getNames() {
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

    private void setStatusIcon(Boolean flag) {
        String path;
        if (flag) {
            path = SwingUtil.class.getClassLoader().getResource(OPEN_ICON).getPath();
        } else {
            path = SwingUtil.class.getClassLoader().getResource(CLOSE_ICON).getPath();
        }
        statusIcon.setIcon(new ImageIcon(path));
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        // ===== swing窗口
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGui();
            }
        });
    }

}
