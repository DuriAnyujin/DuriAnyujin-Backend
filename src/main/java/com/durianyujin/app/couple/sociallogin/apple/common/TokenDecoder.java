package com.durianyujin.app.couple.sociallogin.apple.common;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;

public class TokenDecoder {
    // JWT 문자열에서 payload를 디코딩하고, 이를 특정 클래스의 객체로 변환
    // -> 여러 다양한 클래스 타입으로 변환 가능
    public static <T> T decodePayload(String token, Class<T> targetClass) {

        // JWT 3부분으로 나눠짐
        String[] tokenParts = token.split("\\."); // 정규표현식 "." 나타냄
        // payload indexing
        String payloadJWT = tokenParts[1];
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(payloadJWT));

        ObjectMapper objectMapper = new ObjectMapper()
                // 알려지지 않은 속성이 있는 경우, 예외를 던지지않고, 무시
                // 추가적인 속성이 있어도 역직렬화를 계속 할 수 있음
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            // payload를 targetClass 형식의 java 객체로
            return objectMapper.readValue(payload, targetClass);
        } catch(Exception e) {
            throw new RuntimeException("Error decoding token payload", e);
        }
    }
}
