package cn.link.inkmall.ums;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@RefreshScope
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("cn.link.inkmall.ums.mapper")
public class InkmallUmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(InkmallUmsApplication.class, args);
    }

}
