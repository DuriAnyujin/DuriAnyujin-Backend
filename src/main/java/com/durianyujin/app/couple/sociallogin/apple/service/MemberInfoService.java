package com.durianyujin.app.couple.sociallogin.apple.service;

import com.durianyujin.app.couple.sociallogin.apple.common.TokenDecoder;
import com.durianyujin.app.couple.sociallogin.apple.web.dto.AppleIdTokenPayload;
import com.durianyujin.app.couple.sociallogin.apple.web.dto.CodeResponse;
import com.durianyujin.app.couple.sociallogin.apple.web.dto.MemberInfoRequest;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberInfoService {
    private final AppleLoginUtil appleLoginUtil;

    // 사용자 정보 조회
    public AppleIdTokenPayload getUserInfo (MemberInfoRequest memberInfoRequest) {
        Claims claims = appleLoginUtil.getClaimsBy(memberInfoRequest.getIdentityToken());
        if (claims != null) {
            CodeResponse codeResponse = appleLoginUtil.validateCode(memberInfoRequest.getAuthorizationCode());
            if (codeResponse != null) {
                String idToken = appleLoginUtil.validateRefreshToken(codeResponse.getRefreshToken()).getIdToken();
                return TokenDecoder.decodePayload(idToken, AppleIdTokenPayload.class);
            }
        }
        return null;
    }

    /* 애플 소셜 로그인 탈퇴 로직
    1. 클라이언트는 탈퇴를 진행하기 위해 애플 소셜 로그인 진행
    -> 한번 더 하는 이유? authorization code 유효기간이 5분
    -> 처음 로그인할 때 받은 값을 탈퇴할 때 쓰면 유효시간이 지난 값이 됨
    2. 클라이언트는 서버에게 authorization code를 넘김
    3. 서버는 애플 측에게 accessToken을 받아오는 Rest API 요청
    4. 서버는 애플 측에게 accessToken을 받아서 또 애플 측에게 탈퇴 Rest API 요청
    5. 애플 Rest API에서 200이 내려오면 서버는 자체 회원 탈퇴, DB 업데이트
     */

}
