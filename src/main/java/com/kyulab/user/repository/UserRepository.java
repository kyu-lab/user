package com.kyulab.user.repository;

import com.kyulab.user.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByUserName(String userName);
	boolean existsUserByUserName(String userName);
	boolean deleteUserByUserName(String userName);

}
