package com.yanwu.demo.tcp.server.tcp.server.scheduler;

import com.yanwu.demo.tcp.server.tcp.server.cache.ClientSessionMap;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-14 9:32.
 * <p>
 * description:
 */
@Component
public class SchedulerServer {

    @Scheduled(fixedRate = 1000 * 60)
    public void syncServer() {
        ClientSessionMap.sessionSync();
    }

}
