package com.kyulab.user.service;

import com.kyulab.user.dto.User;
import com.kyulab.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

	private UserRepository userRepository;

	public User saveUser(User user) {
		return userRepository.save(user);
	}

}
