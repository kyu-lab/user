package com.kyulab.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // Null 필드 제외
public class SingleResponse<T> {

	private final int statusCode;
	private final String message;
	private final String desc;
	private final T data;

	@Builder
	public SingleResponse(int statusCode, String message, String desc, T data) {
		this.statusCode = statusCode;
		this.message = message;
		this.desc = desc;
		this.data = data;
	}
}
