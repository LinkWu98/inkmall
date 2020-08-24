package cn.link.inkmall.order.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redession配置
 */
@Configuration
public class RedissonConfig {


    @Bean
    public RedissonClient getRedissonClient() {

        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.153.188:6379");
        return Redisson.create(config);

    }
}