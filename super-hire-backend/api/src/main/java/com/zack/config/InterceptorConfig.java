package com.zack.config;

import com.zack.inteceptor.SMSInteceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Bean
    public SMSInteceptor smsInteceptor(){
        return  new SMSInteceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(smsInteceptor()).addPathPatterns("/passport/getSMSCode");
    }
}
