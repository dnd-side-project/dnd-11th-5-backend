package com.odiga.fiesta.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class loginDTO {

        @Schema(description = "oauth 식별자", nullable = false, example = "3662626307")
        private Long oauthId;

        @Schema(description = "액세스 토큰", nullable = false, example = "eyJ0eXAiOiJKV1QiL...")
        private String accessToken;

        @Schema(description = "리프레시 토큰", nullable = false, example = "eyJ0eXAiOiJKV1QiL...")
        private String refreshToken;
    }
}
