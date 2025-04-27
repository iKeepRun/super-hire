package com.zack.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "exclude")
@PropertySource("classpath:excludeUrlPath.properties")
@Data
public class ExcludeUrlProperties {

    private List<String> urls;
}
