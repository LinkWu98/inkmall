package cn.link.inkmall.gateway.filter;

import cn.link.inkmall.common.utils.JwtUtils;
import cn.link.inkmall.common.utils.RsaUtils;
import cn.link.inkmall.gateway.config.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Author: Link
 * @Date: 2020/6/1 22:45
 * @Version 1.0
 */
@Component
public class AuthGatewayFilter implements GatewayFilter {

    @Autowired
    JwtProperties jwtProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //1. 获取请求中的 cookie 信息
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();

        //若 cookie 不存在，设置错误码返回
        if (CollectionUtils.isEmpty(cookies) || !cookies.containsKey(jwtProperties.getCookieName())) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //2. 解析 jwt
            //cookie中key不止一个value，这里获取第一个
        HttpCookie jwtCookie = cookies.getFirst(jwtProperties.getCookieName());
        String token = jwtCookie.getValue();

        //若 token 不存在，设置错误码返回
        if (StringUtils.isEmpty(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        try {
            JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            //token解析失败，设置错误码返回
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //解析成功，放行
        return chain.filter(exchange);
    }
}
