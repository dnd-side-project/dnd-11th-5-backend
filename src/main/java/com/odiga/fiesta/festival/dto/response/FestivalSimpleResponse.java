package com.odiga.fiesta.festival.dto.response;

import com.odiga.fiesta.festival.domain.Festival;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class FestivalSimpleResponse {

	private Long festivalId;
	private String name;

	public static FestivalSimpleResponse of(Long festivalId, String name) {
		return new FestivalSimpleResponse(festivalId, name);
	}

	public static FestivalSimpleResponse of(Festival festival) {
		return new FestivalSimpleResponse(festival.getId(), festival.getName());
	}
}
