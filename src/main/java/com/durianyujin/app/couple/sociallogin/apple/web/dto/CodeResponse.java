package com.durianyujin.app.couple.sociallogin.apple.web.dto;

import lombok.Getter;

@Getter
public class CodeResponse {

    private String accessToken;

    private String tokenType;

    private Long expiresIn;

    private String refreshToken;

    private String idToken;




}
