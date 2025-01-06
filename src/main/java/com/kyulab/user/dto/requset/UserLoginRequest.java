package com.kyulab.user.dto.requset;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserLoginRequest {

	private String userName;
	private String password;

}
