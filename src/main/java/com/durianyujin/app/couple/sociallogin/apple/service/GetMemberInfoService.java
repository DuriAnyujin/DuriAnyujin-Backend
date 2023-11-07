package com.durianyujin.app.couple.sociallogin.apple.service;

import com.durianyujin.app.couple.sociallogin.apple.common.AppleProperties;
import com.durianyujin.app.couple.sociallogin.apple.common.TokenDecoder;
import com.durianyujin.app.couple.sociallogin.apple.openfeign.AppleAuthClient;
import com.durianyujin.app.couple.sociallogin.apple.web.dto.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.Security;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class GetMemberInfoService {

    private final AppleAuthClient appleAuthClient;

    private final AppleProperties appleProperties;

    public CodeResponse validateCode (String authorizationCode) {
        CodeRequest tokenRequest = CodeRequest.builder()
                .clientId(appleProperties.getClientId())
                .clientSecret(generateClientSecret())
                .grantType(appleProperties.getGrantType())
                .code(authorizationCode)
                .build();

        // member에 refreshToken 저장
        return appleAuthClient.validateCode(tokenRequest);
    }

    public AppleIdTokenPayload getRefreshToken () {
        RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.builder()
                .clientId(appleProperties.getClientId())
                .clientSecret(generateClientSecret())
                .grantType("refresh_token").build();
                //.refreshToken().build(); // 멤버에게 저장된 refreshToken

        String idToken = appleAuthClient.validateRefreshToken(refreshTokenRequest).getIdToken();
        return TokenDecoder.decodePayload(idToken, AppleIdTokenPayload.class);
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


    // https://developer.apple.com/documentation/accountorganizationaldatasharing/creating-a-client-secret
    private String generateClientSecret() {
        Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setHeaderParam("kid", appleProperties.getKeyId()) // kid : 키 식별자
                .setIssuer(appleProperties.getTeamId()) // iss: teamId
                .setAudience(appleProperties.getAudience()) // aud: 애플 사이트
                .setSubject(appleProperties.getClientId()) // sub: client_id
                .setExpiration(expirationDate) // exp: 만료 시간, 6개월 이상 요청 금지
                .setIssuedAt(new Date()) // iat: 생성한 시간
                .signWith(SignatureAlgorithm.ES256, getPrivateKey())
                .compact();

    }

    private PrivateKey getPrivateKey() {
        // Bouncy Castle 암호 라이브러리를 java application에 provider로 추가
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // PEM 형식의 키를 java 키 객체로 변환
        // PEM : Privacy-Enhanced Mail
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC"); // BC: Bouncy Castle

        try {
            // Base64로 인코딩된 개인 키 문자열로 바이트 배열로 디코딩
            byte[] privateKeyBytes = Base64.getDecoder().decode(appleProperties.getPrivateKey());

            // 디코딩된 바이트 배열에서 개인 키 정보 추출
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(privateKeyBytes);

            // PrivateKeyInfo를 PrivateKey 객체로 변환
            return converter.getPrivateKey(privateKeyInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
