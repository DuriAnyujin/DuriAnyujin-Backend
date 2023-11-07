package com.durianyujin.app.couple.sociallogin.apple.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.durianyujin.app.couple.sociallogin.apple.openfeign")
public class OpenFeignConfig {
}
