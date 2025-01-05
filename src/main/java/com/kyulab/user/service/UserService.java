package com.kyulab.user.service;

import com.kyulab.grpc.user.UserExistsRequest;
import com.kyulab.grpc.user.UserExistsResponse;
import com.kyulab.grpc.user.UserServiceGrpc;
import com.kyulab.user.dto.User;
import com.kyulab.user.repository.UserRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@GrpcService
@RequiredArgsConstructor
public class UserService extends UserServiceGrpc.UserServiceImplBase {

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

	@Override
	public void userExists(UserExistsRequest request, StreamObserver<UserExistsResponse> responseObserver) {
		boolean isUser = this.existsUserById(request.getUserId());
		UserExistsResponse response = UserExistsResponse.newBuilder()
				.setExists(isUser)
				.build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
}
