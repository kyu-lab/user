package com.kyulab.user.exception;

import com.kyulab.user.dto.response.SingleResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<SingleResponse<Object>> defaultErrorHandler(HttpServletRequest request, Exception e) {
		SingleResponse<Object> response = SingleResponse.builder()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.message(e.getMessage())
				.desc("fail uri: " + request.getRequestURI())
				.data(null)
				.build();

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

}
