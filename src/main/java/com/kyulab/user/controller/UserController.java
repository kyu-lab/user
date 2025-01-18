package com.kyulab.user.controller;

import com.kyulab.user.dto.response.user.UserResponse;
import com.kyulab.user.service.UserAuthService;
import com.kyulab.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 정보 조회 등 사용자 정보와 관련
 */
@RestController
@RequestMapping("/user/service")
@Tag(name = "사용자 서비스")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping
	@Operation(summary = "유저 목록 조회")
	public ResponseEntity<List<UserResponse>> findUsers() {
		return ResponseEntity.ok().body(userService.findUsers());
	}

	@GetMapping("/{name}")
	@Operation(summary = "특정 유저 정보 조회")
	public ResponseEntity<UserResponse> findUser(@PathVariable String name) {
		Optional<UserResponse> findResult = Optional.of(userService.findUserByName(name));
		if (findResult.isEmpty()) {
			ResponseEntity.badRequest().body("{}");
		}
		return ResponseEntity.ok().body(findResult.get());
	}

}
