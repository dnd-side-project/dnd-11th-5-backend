package com.odiga.fiesta.common;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasicResponse<T> {

	private final int statusCode;
	private final String status; // reason phrase
	private final String message;
	private final T data;

	@Builder
	private BasicResponse(HttpStatus status, String message, T data) {
		this.statusCode = status.value();
		this.status = status.getReasonPhrase();
		this.message = message;
		this.data = data;
	}

	public static <T> BasicResponse<T> of(HttpStatus status, String message, T data) {
		return BasicResponse.<T>builder()
			.status(status)
			.message(message)
			.data(data)
			.build();
	}

	public static <T> BasicResponse<T> of(HttpStatus status, T data) {
		return of(status, status.name(), data);
	}

	public static <T> BasicResponse<T> ok(T data) {
		return of(HttpStatus.OK, data);
	}

	public static <T> BasicResponse<T> ok(String message, T data) {
		return of(HttpStatus.OK, message, data);
	}
}
