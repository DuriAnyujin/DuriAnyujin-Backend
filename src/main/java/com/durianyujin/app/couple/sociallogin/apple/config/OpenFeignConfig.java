package com.durianyujin.app.couple.sociallogin.apple.config;

import com.durianyujin.app.couple.sociallogin.apple.common.AppleFeignErrorDecoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.durianyujin.app.couple.sociallogin.apple.openfeign")
public class OpenFeignConfig {
    @Bean
    public AppleFeignErrorDecoder appleFeignClientErrorDecoder() {
        return new AppleFeignErrorDecoder(new ObjectMapper());
    }
}
