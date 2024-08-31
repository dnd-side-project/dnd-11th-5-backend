package com.odiga.fiesta.user.dto.response;

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
	private Long userTypeId;
}
