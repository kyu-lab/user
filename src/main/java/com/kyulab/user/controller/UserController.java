package com.kyulab.user.controller;

import com.kyulab.user.domain.Users;
import com.kyulab.user.domain.role.TokenType;
import com.kyulab.user.dto.TokenDTO;
import com.kyulab.user.dto.requset.UserLoginRequest;
import com.kyulab.user.dto.response.user.UserLoginResponse;
import com.kyulab.user.dto.requset.UserSaveRequest;
import com.kyulab.user.dto.response.user.UserResponse;
import com.kyulab.user.service.TokenService;
import com.kyulab.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 인증, 가입 등 사용자 상태와 관련
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final TokenService tokenService;

	@GetMapping
	@Operation(summary = "유저 목록 조회")
	public ResponseEntity<List<UserResponse>> findUsers() {
		return ResponseEntity.ok().body(userService.findUsers());
	}

	@GetMapping("/{name}")
	@Operation(summary = "특정 유저 정보 조회")
	public ResponseEntity<UserResponse> findUser(@PathVariable String name) {
		Optional<UserResponse> findResult = Optional.of(userService.findUserByName(name));
		return ResponseEntity.ok().body(findResult.get());
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

	@PostMapping
	@Operation(summary = "유저 생성")
	public ResponseEntity<String> register(@RequestBody UserSaveRequest saveRequest) {
		if (userService.existsByUserName(saveRequest.userName())) {
			return ResponseEntity.badRequest().build();
		}
		userService.saveUser(saveRequest);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{id}")
	@Operation(summary = "id를 이용해 사용자 존재 유무를 반환한다.")
	public boolean existsByid(@PathVariable Long id) {
		return userService.existsById(id);
	}

	@PostMapping("/login")
	@Operation(summary = "유저 로그인")
	public ResponseEntity<UserLoginResponse> login(HttpServletRequest request,
			@RequestBody UserLoginRequest userRequest) throws Exception {
		if (!tokenService.getTokenFromHeader(request).isEmpty()) {
			throw new Exception("토큰이 이미 있는데 으딜 로그인을!");
		}

		Users users = userService.getUsersByRequest(userRequest);
		tokenService.storeRefreshToken(users.getId(), tokenService.createRefreshToken(users));
		TokenDTO tokenDTO = new TokenDTO(String.valueOf(users.getId()), users.getUsername(), users.getAuthorities());
		String accessToken = tokenService.createAccessToken(tokenDTO);
		String responseCookie = tokenService.makeCookieString(TokenType.ACCESS_TOKEN.getType(), accessToken, false);
		UserLoginResponse userResponse = new UserLoginResponse(userRequest.userName());
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, responseCookie)
				.body(userResponse);
	}

}
