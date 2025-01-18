package com.kyulab.user.controller;

import com.kyulab.user.domain.Users;
import com.kyulab.user.domain.role.TokenType;
import com.kyulab.user.dto.TokenDTO;
import com.kyulab.user.dto.requset.UserLoginRequest;
import com.kyulab.user.dto.response.user.UserLoginResponse;
import com.kyulab.user.dto.requset.UserSaveRequest;
import com.kyulab.user.service.TokenService;
import com.kyulab.user.service.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 인증, 가입 등 사용자 상태와 관련
 */
@RestController
@RequestMapping("/user/auth")
@Tag(name = "사용자 인증/인가")
@RequiredArgsConstructor
public class UserAuthController {

	private final UserAuthService userAuthService;
	private final TokenService tokenService;

	@PostMapping
	@Operation(summary = "유저 생성")
	public ResponseEntity<String> register(@RequestBody UserSaveRequest saveRequest) {
		if (userAuthService.existsByUserName(saveRequest.userName())) {
			return ResponseEntity.badRequest().build();
		}
		userAuthService.saveUser(saveRequest);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/login")
	@Operation(summary = "유저 로그인")
	public ResponseEntity<UserLoginResponse> login(HttpServletRequest request,
			@RequestBody UserLoginRequest userRequest) throws Exception {
		if (!tokenService.getTokenFromHeader(request).isEmpty()) {
			throw new Exception("토큰이 이미 있는데 으딜 로그인을!");
		}

		Users users = userAuthService.getUsersByRequest(userRequest);
		tokenService.storeRefreshToken(users.getId(), tokenService.createRefreshToken(users));
		TokenDTO tokenDTO = new TokenDTO(String.valueOf(users.getId()), users.getUsername(), users.getAuthorities());
		String accessToken = tokenService.createAccessToken(tokenDTO);
		String responseCookie = tokenService.makeCookieString(TokenType.ACCESS_TOKEN.getType(), accessToken, false);
		UserLoginResponse userResponse = new UserLoginResponse(userRequest.userName());
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, responseCookie)
				.body(userResponse);
	}

	@GetMapping("/logout")
	@Operation(summary = "유저 로그아웃")
	public ResponseEntity<String> logout(HttpServletRequest request) throws Exception {
		String accessToken = tokenService.getTokenFromHeader(request);
		if (accessToken.isEmpty()) {
			throw new Exception("??? 어케 로그아웃 시도함?");
		}

		String userId = tokenService.getUserIdFromToken(accessToken);
		tokenService.deleteRefreshToken(userId);
		String clearToken = tokenService.makeCookieString(TokenType.ACCESS_TOKEN.getType(), "", true);
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, clearToken)
				.build();
	}

	@PostMapping("/{id}")
	@Operation(summary = "id를 이용해 사용자 존재 유무를 반환한다.")
	public boolean existsByid(@PathVariable Long id) {
		return userAuthService.existsById(id);
	}

}
