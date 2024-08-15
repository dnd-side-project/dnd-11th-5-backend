package com.odiga.fiesta.festival.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FestivalMonthlyResponse {

	private int year;
	private int month;
	private List<DailyFestivalContents> contents;
}
