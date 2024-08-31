package com.odiga.fiesta.user.dto.response;

import com.odiga.fiesta.user.domain.UserType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserTypeResponse {

	private Long userTypeId;
	private String name;

	@Builder
	private UserTypeResponse(Long userTypeId, String name) {
		this.userTypeId = userTypeId;
		this.name = name;
	}

	public static UserTypeResponse of(UserType usertype) {
		return UserTypeResponse.builder()
			.userTypeId(usertype.getId())
			.name(usertype.getName())
			.build();
	}
}
