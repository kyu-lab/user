package com.kyulab.user.service;

import com.kyulab.user.domain.Users;
import com.kyulab.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 애플리케이션에서 공통적으로 사용되는 서비스들
 */
@Service
@RequiredArgsConstructor
public class UserCommonService {

	private final UserRepository userRepository;

	public boolean existsUserByUserName(String userName) {
		return userRepository.existsUserByUserName(userName);
	}

	public Optional<Users> findByUserName(String userName) {
		return userRepository.findByUserName(userName);
	}

	public Optional<Users> findByUserId(long userId) {
		return userRepository.findByUserId(userId);
	}
}
