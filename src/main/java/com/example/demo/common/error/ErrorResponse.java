package com.example.demo.common.error;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

	private int statusCode; // http 상태코드
	private String code; // 커스텀 에러 코드
	private String message;

	@Builder
	private ErrorResponse(int statusCode, String code, String message) {
		this.statusCode = statusCode;
		this.code = code;
		this.message = message;
	}

	public static ErrorResponse of(ErrorCode errorCode) {
		return ErrorResponse.builder()
			.statusCode(errorCode.getStatus())
			.code(errorCode.getCode())
			.message(errorCode.getMessage())
			.build();
	}

	public static ErrorResponse of(int statusCode, String code, String message) {
		return ErrorResponse.builder()
			.statusCode(statusCode)
			.code(code)
			.message(message)
			.build();
	}
}
