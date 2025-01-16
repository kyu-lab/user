package com.kyulab.user.repository;

import com.kyulab.user.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

	Optional<Users> findByName(String name);
	Optional<Users> findById(long id);
	boolean existsUserByName(String name);
	boolean existsById(long name);

}
