package com.kyulab.user.controller;

import com.kyulab.user.dto.User;
import com.kyulab.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name = "사용자 API")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping
	@Operation(summary = "유저 목록 조회")
	public List<User> findUsers() {
		return userService.findUsers();
	}

	@GetMapping("/{userName}")
	@Operation(summary = "특정 유저 조회")
	public ResponseEntity<?> findUser(@PathVariable String userName) {
		if (userService.existsUserByUserName(userName)) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("사용자가 없는데요.?");
		}
		return ResponseEntity.status(HttpStatus.OK)
				.body(userService.findUser(userName));
	}

	@PostMapping
	@Operation(summary = "유저 생성")
	public ResponseEntity<?> saveUser(@RequestBody User user) {
		if (userService.existsUserByUserName(user.getUserName())) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("이미 존재함");
		}
		userService.saveUser(user);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body("생성~");
	}

	@DeleteMapping("/{userName}")
	@Profile("local")
	@Operation(summary = "유저 삭제 (로컬 테스트용)")
	public ResponseEntity<?> deleteUser(@PathVariable String userName) {
		if (userService.existsUserByUserName(userName)) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("사용자가 없음!");
		}
		userService.deleteUserByUserName(userName);
		return ResponseEntity.status(HttpStatus.OK)
				.body("삭제함 ㅜ");
	}

}

