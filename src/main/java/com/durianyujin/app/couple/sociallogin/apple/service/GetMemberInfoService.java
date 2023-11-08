package com.durianyujin.app.couple.sociallogin.apple.service;

import com.durianyujin.app.couple.member.application.MemberService;
import com.durianyujin.app.couple.member.infrastructure.MemberRepository;
import com.durianyujin.app.couple.sociallogin.apple.common.AppleProperties;
import com.durianyujin.app.couple.sociallogin.apple.common.TokenDecoder;
import com.durianyujin.app.couple.sociallogin.apple.openfeign.AppleAuthClient;
import com.durianyujin.app.couple.sociallogin.apple.web.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GetMemberInfoService {

    private final AppleAuthClient appleAuthClient;

    private final AppleProperties appleProperties;

    public Claims getClaimsBy(String identityToken) {

        try {
            ApplePublicKeyResponse response = appleAuthClient.getAppleAuthPublicKey();

            // idToken = jwt token -> 3부분으로 나눠지며, 첫번째 부분은 헤더
            String headerOfIdToken = identityToken.substring(0, identityToken.indexOf("."));

            // 헤더를 디코딩하고, JSON 형식으로 변환하여 header 맴베 저장
            Map<String, String> header = new ObjectMapper().readValue(new String(Base64.getDecoder().decode(headerOfIdToken), "UTF-8"), Map.class);

            // 헤더에서 추출한 kid와 alg 값을 사용하여, response에서 해당하는 키를 찾음
            ApplePublicKeyResponse.Key key = response.getMatchedKeyBy(header.get("kid"), header.get("alg"))
                    .orElseThrow(() -> new NullPointerException("Failed get public key from apples's Id server."));

            byte[] nBytes = Base64.getUrlDecoder().decode(key.getN());
            byte[] eBytes = Base64.getUrlDecoder().decode(key.getE());

            // 바이트 배열 -> BigInteger
            BigInteger n = new BigInteger(1, nBytes);
            BigInteger e = new BigInteger(1, eBytes);

            // RSA 공개 키 스펙을 생성하고, 공개 키를 생성하기위한 키 팩토리 설정
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);

            // key.getKty(): 공개 키 유형 , RSA 키인경우 "RSA"
            KeyFactory keyFactory = KeyFactory.getInstance(key.getKty());

            // RSA 공개 키 생성
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
            // idToken을 서명 검증하고, 해당 토큰 내용을 해석하여 반환, 검증에 사용된 키는 publicKey

            return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(identityToken).getBody();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeySpecException |
                 JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public CodeResponse validateCode (String authorizationCode) {
        CodeRequest tokenRequest = CodeRequest.builder()
                .clientId(appleProperties.getClientId())
                .clientSecret(generateClientSecret())
                .grantType(appleProperties.getGrantType())
                .code(authorizationCode)
                .build();

        // 이후 .. member에 refreshToken 저장
        return appleAuthClient.validateCode(tokenRequest);
    }

    public AppleIdTokenPayload validateRefreshToken (String refreshToken) {
        RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.builder()
                .clientId(appleProperties.getClientId())
                .clientSecret(generateClientSecret())
                .grantType("refresh_token")
                .refreshToken(refreshToken).build(); // 이후.. 멤버에게 저장된 refreshToken

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
