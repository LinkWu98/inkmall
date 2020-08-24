package cn.link.inkmall.order.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Link
 * @Date: 2020/6/5 14:24
 * @Version 1.0
 */
@Component
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolExecutor getThreadPool(@Value("${thread.pool.coreSize}") Integer coreSize,
                                            @Value("${thread.pool.maxSize}") Integer maxSize,
                                            @Value("${thread.pool.aliveTime}") Integer aliveTime,
                                            @Value("${thread.pool.blockingQueueSize}") Integer blockingQueueSize) {

        return new ThreadPoolExecutor(coreSize, maxSize, aliveTime, TimeUnit.MINUTES, new ArrayBlockingQueue<>(blockingQueueSize));


    }

}
