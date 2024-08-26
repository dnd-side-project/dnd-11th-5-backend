package com.odiga.fiesta.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenReissueResponse {

	@Schema(description = "액세스 토큰", example = "eyJ0eXAiOiJKV1QiL...")
	private String accessToken;

	@Schema(description = "리프레시 토큰", example = "eyJ0eXAiOiJKV1QiL...")
	private String refreshToken;

	@Builder
	public TokenReissueResponse(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
}
