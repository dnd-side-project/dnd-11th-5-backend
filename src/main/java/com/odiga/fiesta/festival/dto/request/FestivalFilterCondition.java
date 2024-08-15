package com.odiga.fiesta.festival.dto.request;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FestivalFilterCondition {
	private Set<Long> areas;
	private Set<Integer> months;
	private Set<Long> categories;
}
