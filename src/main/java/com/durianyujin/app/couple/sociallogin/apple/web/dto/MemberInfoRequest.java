package com.durianyujin.app.couple.sociallogin.apple.web.dto;

import lombok.Getter;

@Getter
public class MemberInfoRequest {
    private String identityToken;

    private String authorizationCode;
}
