package com.kyulab.user.domain;


import com.kyulab.user.domain.role.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "USERS")
@DynamicUpdate
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Users implements UserDetails {

	@Id
	@GeneratedValue(generator = "snowflake-id-gen")
	@GenericGenerator(
		name = "snowflake-id-gen",
		strategy = "com.kyulab.user.util.SnowflakeIdGen"
	)
	private long id;

	@Column(unique = true, nullable = false, length = 25)
	private String name;

	@Column(name = "PASS_WORD", nullable = false, length = 100)
	private String passWord;

	@Enumerated(EnumType.STRING)
	private UserRole role;

	public long getId() {
		return id;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.name())); // 현재는 유저당 권한 하나만 부여함
	}

	@Override
	public String getPassword() {
		return passWord;
	}

	@Override
	public String getUsername() {
		return name;
	}

}
