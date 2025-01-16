package com.kyulab.user.controller;

import com.kyulab.user.domain.Users;
import com.kyulab.user.service.UserCommonService;
import com.kyulab.user.service.UserSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 사용자 정보 조회 등 사용자 정보와 관련
 */
@RestController
@RequestMapping("/v1/user/service")
@Tag(name = "사용자 서비스 API v1")
@RequiredArgsConstructor
public class UserServiceController {

	private final UserSearchService userSearchService;
	private final UserCommonService userCommonService;

	@GetMapping
	@Operation(summary = "유저 목록 조회")
	public ResponseEntity<List<Users>> findUsers() {
		return ResponseEntity.ok().body(userSearchService.findUsers());
	}

	@GetMapping("/{userName}")
	@Operation(summary = "특정 유저 정보 조회")
	public ResponseEntity<?> findUser(@PathVariable String userName) {
		if (userCommonService.existsUserByUserName(userName)) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok().body(userSearchService.findUser(userName));
	}

	@PostMapping("/auth-test")
	@Operation(summary = "인증 테스트")
	public ResponseEntity<String> findUser() {
		return ResponseEntity.ok().body("인증됨");
	}

}
