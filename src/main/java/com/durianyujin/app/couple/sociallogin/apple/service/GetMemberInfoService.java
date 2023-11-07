package com.durianyujin.app.couple.sociallogin.apple.service;

import com.durianyujin.app.couple.sociallogin.apple.common.AppleProperties;
import com.durianyujin.app.couple.sociallogin.apple.common.TokenDecoder;
import com.durianyujin.app.couple.sociallogin.apple.openfeign.AppleAuthClient;
import com.durianyujin.app.couple.sociallogin.apple.web.dto.AppleIdTokenPayload;
import com.durianyujin.app.couple.sociallogin.apple.web.dto.TokenRequest;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMException;
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

    public AppleIdTokenPayload get(String authorizationCode) {
        TokenRequest tokenRequest = TokenRequest.builder()
                .clientId(appleProperties.getClientId())
                .clientSecret(generateClientSecret())
                .grantType(appleProperties.getGrantType())
                .code(authorizationCode)
                .build();

        // appleAuthClient의 getIdToken, TokenResponse의 getIdToken
        String idToken = appleAuthClient.getIdToken(tokenRequest).getIdToken();

        return TokenDecoder.decodePayload(idToken, AppleIdTokenPayload.class);
    }

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
