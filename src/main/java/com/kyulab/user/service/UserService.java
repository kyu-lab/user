package com.kyulab.user.service;

import com.kyulab.user.dto.User;
import com.kyulab.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public List<User> findUsers() {
		return userRepository.findAll();
	}

	public Optional<User> findUser(String userName) {
		return userRepository.findByUserName(userName);
	}

	public User saveUser(User user) {
		return userRepository.save(user);
	}

	public boolean existsUserById(int userId) {
		return userRepository.existsById(userId);
	}

	public boolean existsUserByUserName(String userName) {
		return userRepository.existsUserByUserName(userName);
	}

	public void deleteUserByUserName(String userName) {
		userRepository.deleteUserByUserName(userName);
	}

}
