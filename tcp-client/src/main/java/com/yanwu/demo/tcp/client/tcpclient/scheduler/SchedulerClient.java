package com.yanwu.demo.tcp.client.tcpclient.scheduler;

import com.yanwu.demo.tcp.client.tcpclient.handler.Handler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-14 10:46.
 * <p>
 * description:
 */
@Component
public class SchedulerClient {

    @Resource
    private Handler handler;

    /**
     * 模拟心跳
     */
    @Scheduled(fixedRate = 1000 * 30)
    public void syncServer() {
        handler.heartbeat();
    }

}
