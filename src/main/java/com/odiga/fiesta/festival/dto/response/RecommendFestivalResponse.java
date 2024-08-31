package com.odiga.fiesta.festival.dto.response;

import java.util.List;

import com.odiga.fiesta.user.domain.UserType;
import com.odiga.fiesta.user.dto.response.UserTypeResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RecommendFestivalResponse {

	List<FestivalInfo> festivals;
	UserTypeResponse userType;

	@Builder
	private RecommendFestivalResponse(List<FestivalInfo> festivals, UserTypeResponse userType) {
		this.festivals = festivals;
		this.userType = userType;
	}

	public static RecommendFestivalResponse of(List<FestivalInfo> festivals, UserType userType) {
		return RecommendFestivalResponse.builder()
			.festivals(festivals)
			.userType(UserTypeResponse.of(userType))
			.build();
	}
}
