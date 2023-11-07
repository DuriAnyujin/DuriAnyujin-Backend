package com.durianyujin.app.couple.sociallogin.apple.web.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenRequest {

    private String clientId;
    private String clientSecret;
    private String code;
    private String grantType;

    public TokenRequest(String clientId, String clientSecret, String code, String grantType){
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.code = code;
        this.grantType = grantType;
    }
}
