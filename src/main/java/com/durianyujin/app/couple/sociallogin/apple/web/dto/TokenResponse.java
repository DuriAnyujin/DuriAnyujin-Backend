package com.durianyujin.app.couple.sociallogin.apple.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class TokenResponse {

    private String accessToken;

    private String tokenType;

    private Long expiresIn;

    private String refreshToken;

    private String idToken;




}
