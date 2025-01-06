package com.kyulab.user.repository;

import com.kyulab.user.dto.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserDto, Integer> {

	Optional<UserDto> findByUserName(String userName);
	boolean existsUserByUserName(String userName);
	boolean deleteUserByUserName(String userName);

}
