package com.odiga.fiesta.festival.dto.response;

import com.odiga.fiesta.festival.domain.Festival;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FestivalBasicResponse {

	private Long festivalId;
	private String name;

	public FestivalBasicResponse(Long festivalId, String name) {
		this.festivalId = festivalId;
		this.name = name;
	}

	public static FestivalBasicResponse of(Long festivalId, String name) {
		return new FestivalBasicResponse(festivalId, name);
	}

	public static FestivalBasicResponse of(Festival festival) {
		return new FestivalBasicResponse(festival.getId(), festival.getName());
	}
}
