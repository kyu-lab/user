package com.kyulab.user.controller;

import com.kyulab.user.dto.response.SingleResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class ResponseHandller implements ResponseBodyAdvice<Object> {

	@Override
	public boolean supports(MethodParameter returnType,
					Class<? extends HttpMessageConverter<?>> converterType) {
		return MappingJackson2HttpMessageConverter.class.isAssignableFrom(converterType); // 객체일때만 적용
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
		  	Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response) {
		// 중복방지
		if (body instanceof SingleResponse) {
			return body;
		}

		HttpServletResponse servletResponse =
				((ServletServerHttpResponse) response).getServletResponse();

		return SingleResponse.builder()
				.statusCode(servletResponse.getStatus())
				.message("")
				.desc("")
				.data(body)
				.build();
	}
}
