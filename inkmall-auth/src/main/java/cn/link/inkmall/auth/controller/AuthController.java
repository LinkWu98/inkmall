package cn.link.inkmall.auth.controller;

import cn.link.inkmall.auth.config.JwtProperties;
import cn.link.inkmall.auth.service.AuthService;
import cn.link.inkmall.common.bean.ResponseVo;
import cn.link.inkmall.common.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: Link
 * @Date: 2020/6/1 22:10
 * @Version 1.0
 */
@RequestMapping("auth")
@RestController("")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 用户登录授权
     */
    @PostMapping("accredit")
    public ResponseVo accredit(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        String token = authService.accredit(username, password);

        //在控制层将token设置给cookie
        String cookieName = jwtProperties.getCookieName();
        if (!StringUtils.isEmpty(cookieName)) {
            // 解析获取 request 中的 Domain 域名，将 token 设置给 cookie，存入 response 中
            CookieUtils.setCookie(request, response, cookieName, token, jwtProperties.getExpire());
        }

        return ResponseVo.ok();

    }

}
