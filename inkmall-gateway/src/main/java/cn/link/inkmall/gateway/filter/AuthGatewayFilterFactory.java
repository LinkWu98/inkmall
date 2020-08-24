package cn.link.inkmall.gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;

/**
 * @Author: Link
 * @Date: 2020/6/1 22:59
 * @Version 1.0
 */
public class AuthGatewayFilterFactory extends AbstractGatewayFilterFactory {

    @Autowired
    private AuthGatewayFilter authGatewayFilter;

    /**
     * 将 AuthGatewayFilter 注入
     */
    @Override
    public GatewayFilter apply(Object config) {
        return authGatewayFilter;
    }
}
