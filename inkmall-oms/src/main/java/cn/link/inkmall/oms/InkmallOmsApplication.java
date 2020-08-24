package cn.link.inkmall.oms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;

@RefreshScope
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
@MapperScan("cn.link.inkmall.oms.mapper")
public class InkmallOmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(InkmallOmsApplication.class, args);
    }

}
