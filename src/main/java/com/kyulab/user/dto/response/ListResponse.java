package com.kyulab.user.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public class ListResponse<T> {

	private int statusCode;
	private String message;
	private String desc;
	private List<T> data;

}
