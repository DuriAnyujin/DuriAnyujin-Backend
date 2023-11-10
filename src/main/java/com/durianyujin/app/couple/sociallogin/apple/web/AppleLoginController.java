package com.durianyujin.app.couple.sociallogin.apple.web;

import com.durianyujin.app.couple.sociallogin.apple.service.MemberInfoService;
import com.durianyujin.app.couple.sociallogin.apple.web.dto.AppleIdTokenPayload;
import com.durianyujin.app.couple.sociallogin.apple.web.dto.MemberInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/login/apple")
public class AppleLoginController {
    private MemberInfoService userInfoService;

    @GetMapping("/info")
    public ResponseEntity<AppleIdTokenPayload> getMemberInfo(MemberInfoRequest memberInfoRequest) {
        return new ResponseEntity<>(userInfoService.getUserInfo(memberInfoRequest), HttpStatus.OK);
    }
}
