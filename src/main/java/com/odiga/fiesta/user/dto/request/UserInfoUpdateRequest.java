package com.odiga.fiesta.user.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoUpdateRequest {

	private String nickname;
	private String statusMessage;
}
