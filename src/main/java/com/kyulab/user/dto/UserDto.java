package com.kyulab.user.dto;


import com.kyulab.user.dto.role.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Table(name = "users")
@DynamicUpdate
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "user_name", nullable = false, length = 25)
	private String userName;

	@Column(name = "pass_word", nullable = false, length = 100)
	private String password;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "user_role")
	private UserRole userRole;

}
