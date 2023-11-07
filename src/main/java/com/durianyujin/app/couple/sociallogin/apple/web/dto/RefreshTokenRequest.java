package com.durianyujin.app.couple.sociallogin.apple.web.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenRequest {

    private String clientId;
    private String clientSecret;
    private String grantType;

    private String refreshToken;

    public RefreshTokenRequest(String clientId, String clientSecret, String grantType, String refreshToken){
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.grantType = grantType;
        this.refreshToken = refreshToken;
    }
}
