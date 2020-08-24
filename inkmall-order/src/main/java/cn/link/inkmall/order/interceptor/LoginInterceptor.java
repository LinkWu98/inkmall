package cn.link.inkmall.order.interceptor;

import cn.link.inkmall.common.exception.InkmallException;
import cn.link.inkmall.common.utils.CookieUtils;
import cn.link.inkmall.common.utils.JwtUtils;
import cn.link.inkmall.order.bean.UserInfo;
import cn.link.inkmall.order.config.JwtProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author: Link
 * @Date: 2020/6/5 14:08
 * @Version 1.0
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());

        if (!StringUtils.isNotBlank(token)) {
            throw new InkmallException("用户登录信息不存在");
        }

        try {
            Map<String, Object> jwtMap = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            Object userIdObj = jwtMap.get("userId");
            Object usernameObj = jwtMap.get("username");
            UserInfo userInfo = new UserInfo();
            if (userIdObj != null) {
                userInfo.setUserId((Long) userIdObj);
            }

            if (usernameObj != null) {
                userInfo.setUsername((String) usernameObj);
            }

            THREAD_LOCAL.set(userInfo);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new InkmallException("token解析失败，已过期或非法token");
        }


    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        THREAD_LOCAL.remove();
    }

    public static UserInfo getUserInfo() {
        return THREAD_LOCAL.get();
    }


}
