package com.odiga.fiesta.user.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SocialLoginRequest {

	private String id;
	private String email;
	private String accessToken;

	@Builder
	public SocialLoginRequest(String id, String email, String accessToken) {
		this.id = id;
		this.email = email;
		this.accessToken = accessToken;
	}
}
