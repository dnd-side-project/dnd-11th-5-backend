package com.odiga.fiesta.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponse {

	private Long userId;
	private String email;
	private String nickname;
	private String statusMessage;
	private String profileImage;
	private Boolean isProfileCreated;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long userTypeId;
}
