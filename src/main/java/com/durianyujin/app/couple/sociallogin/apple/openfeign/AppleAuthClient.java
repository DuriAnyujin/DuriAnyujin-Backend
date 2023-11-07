package com.durianyujin.app.couple.sociallogin.apple.openfeign;

import com.durianyujin.app.couple.sociallogin.apple.config.OpenFeignConfig;
import com.durianyujin.app.couple.sociallogin.apple.web.dto.RefreshTokenRequest;
import com.durianyujin.app.couple.sociallogin.apple.web.dto.RefreshTokenResponse;
import com.durianyujin.app.couple.sociallogin.apple.web.dto.CodeRequest;
import com.durianyujin.app.couple.sociallogin.apple.web.dto.CodeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(
        name = "apple-auth", // 임의의 client 이름
        url = "${apple.auth.url}", // 호출할 주소 -> application.properties에
        configuration = OpenFeignConfig.class
)


public interface AppleAuthClient {
    // authorization code 검증 후, refreshtoken 발급받기
    @PostMapping("/auth/token")
    CodeResponse validateCode (
            @RequestBody CodeRequest tokenRequest
            );

    // refreshToken을 검증 후, 재발급 받을 수 있는지 확인 -> accessToken 재발급 -> 로그인 성공
    @PostMapping("/auth/token")
    RefreshTokenResponse validateRefreshToken (
            @RequestBody RefreshTokenRequest refreshTokenRequest
            );

//    @PostMapping("/auth/revoke")

}