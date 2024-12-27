package com.kyulab.user.dto;


import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Table(name = "users")
@DynamicUpdate
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "user_name", nullable = false, length = 25)
	private String userName;

	@Column(name = "pass_word", nullable = false, length = 50)
	private String password;

}
