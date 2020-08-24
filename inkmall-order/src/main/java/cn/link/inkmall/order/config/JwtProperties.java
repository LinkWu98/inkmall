package cn.link.inkmall.order.config;

import cn.link.inkmall.common.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

/**
 * @Author: Link
 * @Date: 2020/6/5 14:09
 * @Version 1.0
 */
@Data
@Slf4j
@Component
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
            log.error("生成 publicKey 失败");
        }

    }

}
