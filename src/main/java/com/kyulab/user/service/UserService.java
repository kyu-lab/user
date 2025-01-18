package com.kyulab.user.service;

import com.kyulab.user.domain.Users;
import com.kyulab.user.dto.response.user.UserResponse;
import com.kyulab.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public List<UserResponse> findUsers() {
		List<Users> userList = userRepository.findAll();
		if (userList.isEmpty()) {
			return new ArrayList<>();
		}
		return userList.stream()
				.map(user -> new UserResponse(user.getUsername()))
				.toList();
	}

	public UserResponse findUserByName(String name) {
		Optional<Users> result = userRepository.findByName(name);
		if (result.isEmpty()) {
			return null;
		}
		Users users = result.get();
		return new UserResponse(users.getUsername());
	}

}
