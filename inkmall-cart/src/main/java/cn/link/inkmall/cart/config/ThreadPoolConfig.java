package cn.link.inkmall.cart.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Link
 * @Date: 2020/6/2 20:41
 * @Version 1.0
 */
@Configuration
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolExecutor initThreadPoolExecutor(@Value("thread.pool.corePoolSize") Integer corePoolSize,
                                                     @Value("thread.pool.maximumPoolSize") Integer maximumPoolSize,
                                                     @Value("thread.pool.keepAliveTime") Long keepAliveTime,
                                                     @Value("thread.pool.blockingQueueSize") Integer blockingQueueSize) {

        return new ThreadPoolExecutor(
                corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MINUTES, new ArrayBlockingQueue<>(blockingQueueSize)
        );

    }


}
