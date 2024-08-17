package com.odiga.fiesta.festival.dto.response;

import com.odiga.fiesta.festival.domain.Festival;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FestivalBasic {

	private Long festivalId;
	private String name;

	public FestivalBasic(Long festivalId, String name) {
		this.festivalId = festivalId;
		this.name = name;
	}

	public static FestivalBasic of(Long festivalId, String name) {
		return new FestivalBasic(festivalId, name);
	}

	public static FestivalBasic of(Festival festival) {
		return new FestivalBasic(festival.getId(), festival.getName());
	}
}
