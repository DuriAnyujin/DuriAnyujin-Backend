package com.durianyujin.app.couple.sociallogin.apple.common;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "provider.apple")
@Getter
@Setter // 이걸 추가해야 binding 에러가 안뜸
public class AppleProperties {

    private String grantType;
    private String clientId;
    private String keyId;
    private String teamId;
    private String audience;
    private String privateKey;


}
