package com.durianyujin.app.couple;

import com.durianyujin.app.couple.sociallogin.apple.service.AppleLoginUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("default")
class CoupleApplicationTests {
	@Autowired
	AppleLoginUtil getMemberInfoService;

	@Test
	void getToken() {
		String authorizationCode = "클라이언트로 부터 받은 애플 인가코드";

		var source = getMemberInfoService.validateCode(authorizationCode);
	}


}
