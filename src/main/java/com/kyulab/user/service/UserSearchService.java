package com.kyulab.user.service;

import com.kyulab.user.domain.Users;
import com.kyulab.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserSearchService {

	private final UserRepository userRepository;

	public List<Users> findUsers() {
		return userRepository.findAll();
	}

	public Optional<Users> findUser(String userName) {
		return userRepository.findByUserName(userName);
	}

}
