package cn.link.inkmall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author: Link
 * @Date: 2020/6/1 22:06
 * @Version 1.0
 */

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class InkmallAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(InkmallAuthApplication.class, args);
    }
}
