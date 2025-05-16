package com.zack.config;

import com.zack.inteceptor.JwtCurrentUserInteceptor;

import com.zack.inteceptor.SMSInteceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置类
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Bean
    public SMSInteceptor smsInteceptor(){
        return  new SMSInteceptor();
    }
    @Bean
    public JwtCurrentUserInteceptor jwtCurrentUserInteceptor(){
        return  new JwtCurrentUserInteceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(smsInteceptor()).addPathPatterns("/passport/getSMSCode");
        registry.addInterceptor(jwtCurrentUserInteceptor()).addPathPatterns("/**");
    }
}
