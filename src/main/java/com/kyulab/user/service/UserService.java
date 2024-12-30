package com.kyulab.user.service;

import com.kyulab.HelloReply;
import com.kyulab.HelloRequest;
import com.kyulab.MyServiceGrpc;
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
public class UserService extends MyServiceGrpc.MyServiceImplBase {

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
	public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
		String msg = "grpc 테스트용 입니당";
		System.out.println("난 실행이 되었지롱?");
		HelloReply reply = HelloReply.newBuilder().setMessage(msg).build();
		responseObserver.onNext(reply);
		responseObserver.onCompleted();
	}
}
