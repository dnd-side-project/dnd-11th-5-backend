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

        @Schema(description = "oauth id", nullable = false, example = "3662626307")
        private Long oauthId;

        @Schema(description = "액세스 토큰", nullable = false, example = "eyJ0eXAiOiJKV1QiL...")
        private String accessToken;

        @Schema(description = "리프레시 토큰", nullable = false, example = "eyJ0eXAiOiJKV1QiL...")
        private String refreshToken;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class createProfileDTO {

        @Schema(description = "유저 유형 id", nullable = false, example = "1")
        private Long userTypeId;

        @Schema(description = "유저 유형 명칭", nullable = false, example = "로맨티스트")
        private String userTypeName;

        @Schema(description = "유형에 해당되는 프로필 이미지", nullable = false, example = "https://fiesta-image.s3.ap-northeast-2.amazonaws.com/user_type/user_type_1.png")
        private String userTypeImage;

        @Schema(description = "액세스 토큰", nullable = false, example = "eyJ0eXAiOiJKV1QiL...")
        private String accessToken;

        @Schema(description = "리프레시 토큰", nullable = false, example = "eyJ0eXAiOiJKV1QiL...")
        private String refreshToken;
    }
}
