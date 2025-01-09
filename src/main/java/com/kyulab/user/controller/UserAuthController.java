package com.kyulab.user.controller;

import com.kyulab.user.service.UserCommonService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/auth")
@Tag(name = "사용자 인증 API")
@RequiredArgsConstructor
public class UserAuthController {

	private final UserCommonService commonService;

	@PostMapping("/{userId}")
	public boolean test(@PathVariable Long userId) {
		return commonService.existsByUserId(userId);
	}

}
