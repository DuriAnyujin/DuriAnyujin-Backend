package com.durianyujin.app.couple.sociallogin.apple.web.dto;

import lombok.Getter;

@Getter
public class RefreshTokenResponse {

    private String accessToken;

    private String tokenType;

    private String expiresIn;

    private String idToken;
}

