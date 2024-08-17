package com.odiga.fiesta.festival.dto.request;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FestivalFilterRequest {

	private List<Long> areas; // 시도 단위
	private List<Integer> months;
	private List<Long> categories;

	@Builder
	public FestivalFilterRequest(List<Long> areas, List<Integer> months, List<Long> categories) {
		this.areas = areas;
		this.months = months;
		this.categories = categories;
	}
}
