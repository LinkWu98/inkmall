package cn.link.inkmall.oms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Link
 * @Date: 2020/6/6 23:55
 * @Version 1.0
 */
@Component
@Data
@ConfigurationProperties("thread.pool")
public class ThreadPoolConfig {

    private Integer corePoolSize;
    private Integer maximumPoolSize;
    private Integer keepAliveTime;
    private Integer blockingQueueSize;

    @Bean
    public ThreadPoolExecutor getThreadPool() {

        return new ThreadPoolExecutor(
                corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MINUTES, new ArrayBlockingQueue<>(blockingQueueSize)
        );

    }

}
