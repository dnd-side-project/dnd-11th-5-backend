package com.odiga.fiesta.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class KakaoProfile {

	private Long id;

	@JsonProperty("connected_at")
	private String connectedAt;

	private Properties properties;

	@JsonProperty("kakao_account")
	private KakaoAccount kakaoAccount;

	@Data
	public static class Properties {
		private String nickname;

		@JsonProperty("profile_image")
		private String profileImage;

		@JsonProperty("thumbnail_image")
		private String thumbnailImage;
	}

	@Data
	public static class KakaoAccount {
		@JsonProperty("profile_nickname_needs_agreement")
		private boolean profileNicknameNeedsAgreement;

		@JsonProperty("profile_image_needs_agreement")
		private boolean profileImageNeedsAgreement;
		private Profile profile;

		@JsonProperty("has_email")
		private boolean hasEmail;

		@JsonProperty("email_needs_agreement")
		private boolean emailNeedsAgreement;

		@JsonProperty("is_email_valid")
		private boolean isEmailValid;

		@JsonProperty("is_email_verified")
		private boolean isEmailVerified;
		private String email;

		@Data
		public static class Profile {
			private String nickname;

			@JsonProperty("thumbnail_image_url")
			private String thumbnailImageUrl;

			@JsonProperty("profile_image_url")
			private String profileImageUrl;

			@JsonProperty("is_default_image")
			private boolean isDefaultImage;

			@JsonProperty("is_default_nickname")
			private boolean isDefaultNickname;
		}
	}
}
