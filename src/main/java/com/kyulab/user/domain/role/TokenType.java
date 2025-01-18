package com.kyulab.user.domain.role;

public enum TokenType {

	ACCESS_TOKEN("ACCESS"),
	REFRESH_TOKEN("REFRESH");

	final String type;

	TokenType(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}

}
