package cn.link.inkmall.auth.config;

import cn.link.inkmall.common.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.io.*;

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

    private String privateKeyPath;

    private String publicKeyPath;

    private String secret;

    private String cookieName;

    /**
     * jwt 过期时间，单位：分钟
     */
    private Integer expire;

    private PublicKey publicKey;

    private PrivateKey privateKey;


    @PostConstruct
    public void init() {

        try {
            File privateKeyFile = new File(privateKeyPath);
            File publicKeyFile = new File(publicKeyPath);

            if (!privateKeyFile.exists() || !publicKeyFile.exists()) {
                //如果有一个密钥文件不存在，就重新创建密钥
                RsaUtils.generateKey(publicKeyPath, privateKeyPath, secret);
            }

            this.privateKey = RsaUtils.getPrivateKey(privateKeyPath);
            this.publicKey = RsaUtils.getPublicKey(publicKeyPath);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("密钥创建失败");
        }


    }



}
