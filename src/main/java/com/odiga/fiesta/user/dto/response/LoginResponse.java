package com.odiga.fiesta.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponse {

	private String accessToken;
	private String refreshToken;
	private Boolean isProfileRegistered;

	@Builder
	private LoginResponse(String accessToken, String refreshToken, Boolean isProfileRegistered) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.isProfileRegistered = isProfileRegistered;
	}
}
