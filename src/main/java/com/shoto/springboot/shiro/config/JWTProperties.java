package com.shoto.springboot.shiro.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 自定义JWT属性配置文件，用于注入配置文件中的值
 */
@Data
@Configuration
@Component
@ConfigurationProperties(prefix = "jwt.config")
public class JWTProperties {
    /**
     * jwt加密秘钥
     */
    private String secret;
    /**
     * jwt有效时间
     */
    private long timeout;
    /**
     * 过滤不需要认证的URL
     */
    private String noAuthUrl;
}
