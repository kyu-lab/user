package com.kyulab.user.domain;


import com.kyulab.user.domain.role.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "users")
@DynamicUpdate
@Builder
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Users {

	@Id
	@GeneratedValue(generator = "snowflake-id-gen")
	@GenericGenerator(
			name = "snowflake-id-gen",
			strategy = "com.kyulab.user.util.SnowflakeIdGen"
	)
	private long id;

	@Column(nullable = false, length = 25)
	private String name;

	@Column(name = "PASS_WORD", nullable = false, length = 100)
	private String passWord;

	@Enumerated(EnumType.STRING)
	private UserRole role;

}
