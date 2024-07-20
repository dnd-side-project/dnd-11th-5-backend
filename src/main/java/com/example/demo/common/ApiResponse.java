package com.example.demo.common;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

	private int code;
	private HttpStatus status;
	private String message;
	private T data;

	@Builder
	private ApiResponse(HttpStatus status, String message, T data) {
		this.code = status.value();
		this.status = status;
		this.message = message;
		this.data = data;
	}

	public static <T> ApiResponse<T> of(HttpStatus status, String message, T data) {
		return ApiResponse.<T>builder()
			.status(status)
			.message(message)
			.data(data)
			.build();
	}

	public static <T> ApiResponse<T> of(HttpStatus status, T data) {
		return of(status, status.name(), data);
	}

	public static <T> ApiResponse<T> ok(T data) {
		return of(HttpStatus.OK, data);
	}

	public static <T> ApiResponse<T> ok(String message, T data) {
		return of(HttpStatus.OK, message, data);
	}
}
