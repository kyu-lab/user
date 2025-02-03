package com.kyulab.user.service;

import com.kyulab.user.domain.Users;
import com.kyulab.user.dto.requset.UserLoginRequest;
import com.kyulab.user.dto.requset.UserSaveRequest;
import com.kyulab.user.domain.role.UserRole;
import com.kyulab.user.dto.response.user.UserResponse;
import com.kyulab.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public void saveUser(UserSaveRequest saveRequest) {
		 Users newUsers = Users.builder()
				.name(saveRequest.userName())
				.passWord(passwordEncoder.encode(saveRequest.passWord()))
				.role(UserRole.USER) // USER로 고정
				.build();
		userRepository.save(newUsers);
	}

	public Users getUsersByRequest(UserLoginRequest request) throws Exception {
		Users users = loadUserByUsername(request.userName());
		if (!passwordEncoder.matches(request.passWord(), users.getPassword())) {
			throw new Exception("Wrong Password");
		}
		return users;
	}

	@Override
	public Users loadUserByUsername(String userName) throws UsernameNotFoundException {
		return userRepository.findByName(userName)
				.orElseThrow(() -> new UsernameNotFoundException("Username not found: " + userName));
	}

	public boolean existsById(long userId) {
		return userRepository.existsById(userId);
	}

	public boolean existsByUserName(String userName) {
		return userRepository.existsUserByName(userName);
	}

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
