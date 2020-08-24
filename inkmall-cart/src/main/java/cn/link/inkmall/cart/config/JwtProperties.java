package cn.link.inkmall.cart.config;

import cn.link.inkmall.common.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

/**
 * @Author: Link
 * @Date: 2020/6/2 20:05
 * @Version 1.0
 */
@ConfigurationProperties("jwt")
@Component
@Data
@Slf4j
public class JwtProperties {

    private String publicKeyPath;

    private String cookieName;

    private Integer userKeyCookieExpire;

    private String userKeyCookieName;

    private PublicKey publicKey;

    @PostConstruct
    public void init() {

        try {
            this.publicKey = RsaUtils.getPublicKey(publicKeyPath);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("公钥生成失败");
        }

    }


}
