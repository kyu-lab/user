package com.kyulab.user.domain;


import com.kyulab.user.dto.role.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "users")
@DynamicUpdate
@Builder
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Users {

	/**
	 * @see com.kyulab.user.util.UserSecurityUtil
	 * nextId 메서드를 이용해 아이디 생성중
	 */
	@Id
	@Column(name = "user_id", nullable = false)
	private long userId;

	@Column(name = "user_name", nullable = false, length = 25)
	private String userName;

	@Column(name = "pass_word", nullable = false, length = 100)
	private String password;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "user_role")
	private UserRole userRole;

}
