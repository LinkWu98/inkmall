package cn.link.inkmall.pms;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableFeignClients
@EnableSwagger2
@EnableDiscoveryClient
@MapperScan("cn.link.inkmall.pms.mapper")
@RefreshScope
@SpringBootApplication
public class InkmallPmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(InkmallPmsApplication.class, args);
    }

}
