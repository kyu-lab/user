package com.kyulab.user.controller;

import com.kyulab.user.config.JwtTokenProvider;
import com.kyulab.user.domain.Users;
import com.kyulab.user.dto.requset.UserLoginRequest;
import com.kyulab.user.dto.requset.UserSaveRequest;
import com.kyulab.user.service.UserAuthService;
import com.kyulab.user.service.UserCommonService;
import com.kyulab.user.service.UserRedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 인증, 가입 등 사용자 상태와 관련
 */
@RestController
@RequestMapping("/user/auth")
@Tag(name = "사용자 인증 API")
@RequiredArgsConstructor
public class UserAuthController {

	private final UserAuthService userAuthService;
	private final UserCommonService userCommonService;
	private final UserRedisService redisService;
	private final JwtTokenProvider jwtTokenProvider;

	@PostMapping
	@Operation(summary = "유저 생성")
	public ResponseEntity<String> saveUser(@RequestBody UserSaveRequest saveRequest) {
		if (userCommonService.existsUserByUserName(saveRequest.getUserName())) {
			return ResponseEntity.badRequest().build();
		}
		userAuthService.saveUser(saveRequest);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/login")
	@Operation(summary = "유저 로그인")
	public ResponseEntity<String> loginUser(HttpServletRequest request, HttpServletResponse response, @RequestBody UserLoginRequest userRequest) {
		if (request.getCookies() != null) {
			for (Cookie c : request.getCookies()) {
				if (c.getName().equals("refreshToken")) {
					return ResponseEntity.badRequest().build();
				}
			}
		}

		if (!userAuthService.checkUserByUserDetails(userRequest)) {
			return ResponseEntity.badRequest().build();
		}

		Users users = userCommonService.findByUserName(userRequest.getUserName()).get();
		if (redisService.getFromRedis(String.valueOf(users.getUserId())) != null) {
			return ResponseEntity.badRequest().build();
		}
		String refreshToken = jwtTokenProvider.createRefreshToken(users);
		String accessToken = " Bearer: " +  jwtTokenProvider.createAccessToken(users);
		ResponseCookie responseCookie = ResponseCookie.from("refreshToken")
													.value(refreshToken)
													.httpOnly(true)
													.secure(false)
													.maxAge(1000)
													.build();

		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, responseCookie.toString())
				.body(accessToken);
	}

	@PostMapping("/logout")
	@Operation(summary = "유저 로그아웃")
	public ResponseEntity<String> logoutUser(HttpServletRequest request) {
		if (request.getCookies() == null) {
			return ResponseEntity.badRequest().build();
		}

		String refreshToken = "";
		for (Cookie c : request.getCookies()) {
			if (c.getName().equals("refreshToken")) {
				refreshToken = c.getValue();
				break;
			}
		}

		String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
		ResponseCookie responseCookie = ResponseCookie.from("refreshToken")
										.maxAge(0)
										.build();
		redisService.deleteFromRedis(userId); // 레디스에서 키 삭제
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, responseCookie.toString())
				.build();
	}

}

