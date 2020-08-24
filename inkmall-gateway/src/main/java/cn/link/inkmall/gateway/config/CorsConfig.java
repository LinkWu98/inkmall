package cn.link.inkmall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter getCorsWebFilter() {

        //初始化一个跨域配置类
        CorsConfiguration configuration = new CorsConfiguration();
        //允许跨域的域名，不推荐写*，因为不能携带Cookie
        configuration.addAllowedOrigin("http://manager.gmall.com");
        configuration.addAllowedOrigin("http://manager.inkmall.com");
        configuration.setAllowCredentials(true); //是否可以携带cookie
        configuration.addAllowedHeader("*");//允许所有的请求头信息
        configuration.addAllowedMethod("*");//允许所有请求方法跨域访问

        //初始化一个跨域配置源
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();

        //拦截哪些路径？所有，因为访问网关必然都是跨域
        configurationSource.registerCorsConfiguration("/**", configuration);

        //初始化跨域过滤器
        return new CorsWebFilter(configurationSource);

    }

}
