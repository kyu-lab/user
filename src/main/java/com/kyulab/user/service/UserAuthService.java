package com.kyulab.user.service;

import com.kyulab.user.domain.Users;
import com.kyulab.user.dto.requset.UserLoginRequest;
import com.kyulab.user.dto.requset.UserSaveRequest;
import com.kyulab.user.domain.role.UserRole;
import com.kyulab.user.repository.UserRepository;
import com.kyulab.user.util.SnowflakeIdGen;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthService implements UserDetailsService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public void saveUser(UserSaveRequest saveRequest) {
		 Users newUsers = Users.builder()
				.name(saveRequest.getUserName())
				.passWord(passwordEncoder.encode(saveRequest.getPassWord()))
				.role(UserRole.USER) // USER로 고정
				.build();
		userRepository.save(newUsers);
	}

	public boolean checkUserByUserDetails(UserLoginRequest request) {
		UserDetails userDetails = loadUserByUsername(request.getUserName());
		return passwordEncoder.matches(request.getPassword(), userDetails.getPassword());
	}

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		return userRepository.findByName(userName)
				.map(this::createUserDetails)
				.orElseThrow(() -> new UsernameNotFoundException("Username not found: " + userName));
	}

	private UserDetails createUserDetails(Users users) {
		return org.springframework.security.core.userdetails.User.builder()
				.username(users.getName())
				.password(users.getPassWord())
				.build();
	}
}
