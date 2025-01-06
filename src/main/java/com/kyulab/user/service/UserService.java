package com.kyulab.user.service;

import com.kyulab.grpc.user.UserExistsRequest;
import com.kyulab.grpc.user.UserExistsResponse;
import com.kyulab.grpc.user.UserServiceGrpc;
import com.kyulab.user.dto.UserDto;
import com.kyulab.user.dto.requset.UserLoginRequest;
import com.kyulab.user.dto.role.UserRole;
import com.kyulab.user.repository.UserRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@GrpcService
@RequiredArgsConstructor
public class UserService extends UserServiceGrpc.UserServiceImplBase implements UserDetailsService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public List<UserDto> findUsers() {
		return userRepository.findAll();
	}

	public Optional<UserDto> findUser(String userName) {
		return userRepository.findByUserName(userName);
	}

	public UserDto saveUser(UserDto userDto) {
		String ordignalPwd = userDto.getPassword();
		UserDto user = new UserDto().builder()
				.userName(userDto.getUserName())
				.password(passwordEncoder.encode(ordignalPwd))
				.userRole(UserRole.USER)
				.build();
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

	// 외부 통신용으로 분리 필요
	@Override
	public void userExists(UserExistsRequest request, StreamObserver<UserExistsResponse> responseObserver) {
		boolean isUser = this.existsUserById(request.getUserId());
		UserExistsResponse response = UserExistsResponse.newBuilder()
				.setExists(isUser)
				.build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	public boolean checkUserByUserDetails(UserLoginRequest request) {
		UserDetails userDetails = loadUserByUsername(request.getUserName());
		return passwordEncoder.matches(request.getPassword(), userDetails.getPassword());
	}

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		return userRepository.findByUserName(userName)
				.map(this::createUserDetails)
				.orElseThrow(() -> new UsernameNotFoundException("Username not found: " + userName));
	}

	private UserDetails createUserDetails(UserDto userDto) {
		return User.builder()
				.username(userDto.getUserName())
				.password(userDto.getPassword())
				.build();
	}
}
