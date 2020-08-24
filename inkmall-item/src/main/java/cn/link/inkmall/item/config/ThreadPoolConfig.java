package cn.link.inkmall.item.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @Author: Link
 * @Date: 2020/6/1 9:25
 * @Version 1.0
 */
@Configuration
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolExecutor getThreadPool(@Value("${thread.pool.coreSize}") Integer coreSize,
                                            @Value("${thread.pool.maxSize}") Integer maxSize,
                                            @Value("${thread.pool.aliveTime}") Long aliveTime,
                                            @Value("${thread.pool.blockingQueueSize}") Integer blockingQueueSize) {

        return new ThreadPoolExecutor(
                coreSize,
                maxSize,
                aliveTime,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(blockingQueueSize)
        );
    }
}
