package com.zack.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
@Component
@PropertySource("classpath:jwt.properties")
@ConfigurationProperties(prefix = "auth")
@Data
public class JWTProperties {
    private String key;
}
