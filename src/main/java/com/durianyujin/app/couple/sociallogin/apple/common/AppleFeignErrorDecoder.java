package com.durianyujin.app.couple.sociallogin.apple.common;


import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;


// feign.codec.ErrorDecoder는 Feign에서 발생하는 HTTP 응답 오류를 처리하기 위한 인터페이스
// 기본적으로 Feign은 ErrorDecoder.Default를 사용하여 HTTP 응답 코드에 따라 예외를 생성합
// 하지만 사용자 정의 ErrorDecoder(MyServerException)를 구현하여 특정 응답에 대해 사용자 지정 예외를 생성하거나 처리가능
@Slf4j
@RequiredArgsConstructor
public class AppleFeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    /**
     * 애플 소셜 로그인 Feign API 연동 시 발생되는 오류에 대해서 예외 처리를 수행.
     *
     * @param methodKey Feign Client 메서드 이름
     * @param response  응답 정보
     * @return 예외를 리턴한다
     */
    @Override
    public Exception decode(String methodKey, Response response) {
        Object body = null;
        if (response != null && response.body() != null) {
            try {
                body = objectMapper.readValue(response.body().toString(), Object.class);
            } catch (IOException e) {
                log.error("Error decoding response body", e);
            }
        }

        log.error("애플 소셜 로그인 Feign API Feign Client 호출 중 오류가 발생되었습니다. body: {}", body);

        return new MyServerException(400, "애플 소셜 로그인 Feign API Feign Client 호출 오류");
    }
}
