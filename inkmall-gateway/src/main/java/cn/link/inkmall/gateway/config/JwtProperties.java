package cn.link.inkmall.gateway.config;

import cn.link.inkmall.common.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @Author: Link
 * @Date: 2020/6/1 22:22
 * @Version 1.0
 */
@Slf4j
@Component
@Data
@ConfigurationProperties("jwt")
public class JwtProperties {

    private String publicKeyPath;

    private String cookieName;

    private PublicKey publicKey;


    @PostConstruct
    public void init() {

        try {
            this.publicKey = RsaUtils.getPublicKey(publicKeyPath);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("网关服务公钥设置失败");
        }

    }



}
