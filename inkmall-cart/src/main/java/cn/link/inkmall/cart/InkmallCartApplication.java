package cn.link.inkmall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author: Link
 * @Date: 2020/6/1 23:43
 * @Version 1.0
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class InkmallCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(InkmallCartApplication.class, args);
    }

}
