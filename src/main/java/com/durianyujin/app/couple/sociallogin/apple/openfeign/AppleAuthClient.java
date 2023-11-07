package com.durianyujin.app.couple.sociallogin.apple.openfeign;

import com.durianyujin.app.couple.sociallogin.apple.config.OpenFeignConfig;
import com.durianyujin.app.couple.sociallogin.apple.web.dto.TokenRequest;
import com.durianyujin.app.couple.sociallogin.apple.web.dto.TokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(
        name = "apple-auth", // 임의의 client 이름
        url = "${apple.auth.url}", // 호출할 주소 -> application.properties에
        configuration = OpenFeignConfig.class
)


public interface AppleAuthClient {

    @PostMapping("/auth/token")
    TokenResponse getIdToken(
            @RequestBody TokenRequest tokenRequest
            );
}