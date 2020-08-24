package cn.link.inkmall.cart.interceptor;

import cn.link.inkmall.cart.bean.UserInfo;
import cn.link.inkmall.cart.config.JwtProperties;
import cn.link.inkmall.common.utils.CookieUtils;
import cn.link.inkmall.common.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

/**
 * 购物车控制器的拦截器，用于用户登录情况的校验
 *
 * @Author: Link
 * @Date: 2020/6/2 19:36
 * @Version 1.0
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    public static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>();

    private static final Logger log = LoggerFactory.getLogger(LoginInterceptor.class);

    @Autowired
    JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        UserInfo userInfo = new UserInfo();

        //1. 从请求中 cookie 中的 token 和 user-key
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        String userKey = CookieUtils.getCookieValue(request, jwtProperties.getUserKeyCookieName());

        //2. userKey 为空就设置一个
        if (StringUtils.isBlank(userKey)) {
            userKey = UUID.randomUUID().toString();
            response.addCookie(new Cookie(jwtProperties.getUserKeyCookieName(), userKey));
        }

        userInfo.setUserKey(userKey);

        //3. 解析 token
        if (StringUtils.isNotBlank(token)) {
            try {
                Map<String, Object> tokenMap = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
                Object userId = tokenMap.get("userId");
                if (userId != null) {
                    //若有 token ，将 userId 存入
                    userInfo.setUserId(Long.valueOf(userId.toString()));
                }
            } catch (Exception e) {
                //解析失败，只有 userKey
                e.printStackTrace();
            }
        }

        //4. 设置用户信息到 ThreadLocal中
        THREAD_LOCAL.set(userInfo);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //tomcat使用线程池，因此线程不会被释放，需要手动释放资源防止内存泄漏
        THREAD_LOCAL.remove();
    }

    public static UserInfo getUserInfo() {

        return THREAD_LOCAL.get();

    }
}
