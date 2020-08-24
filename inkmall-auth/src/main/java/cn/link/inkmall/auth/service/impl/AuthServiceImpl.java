package cn.link.inkmall.auth.service.impl;

import cn.link.inkmall.auth.config.JwtProperties;
import cn.link.inkmall.auth.feign.UmsFeign;
import cn.link.inkmall.auth.service.AuthService;
import cn.link.inkmall.common.exception.InkmallException;
import cn.link.inkmall.common.utils.JwtUtils;
import cn.link.inkmall.ums.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Link
 * @Date: 2020/6/1 22:14
 * @Version 1.0
 */
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UmsFeign umsFeign;

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public String accredit(String username, String password) {

        //1. 远程调用，验证用户信息
        UserEntity userEntity = umsFeign.doLogin(username, password).getData();
        if (userEntity == null) {
            throw new InkmallException("账号或密码错误");
        }

        //2. 验证用户成功，生成 token
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userEntity.getId());
        map.put("username", userEntity.getUsername());
        String token = null;

        try {
            token = JwtUtils.generateToken(map, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
        } catch (Exception e) {
            e.printStackTrace();
            throw new InkmallException("jwt 生成失败");
        }

        return token;

    }
}
