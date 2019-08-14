package com.yanwu.demo.tcp.server.tcp.server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-09 11:25.
 * <p>
 * description:
 */
@EnableScheduling
@SpringBootApplication
public class TcpServerApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(TcpServerApplication.class);
        builder.headless(false).run(args);
    }

    @Bean
    public Executor nettyExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // ----- 设置核心线程数
        executor.setCorePoolSize(50);
        // ----- 设置最大线程数
        executor.setMaxPoolSize(100);
        // ----- 设置队列容量
        executor.setQueueCapacity(Integer.MAX_VALUE);
        // ----- 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(120);
        // ----- 设置默认线程名称
        executor.setThreadNamePrefix("netty-pool-");
        // ----- 设置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // ----- 执行初始化
        executor.initialize();
        return executor;
    }
}
