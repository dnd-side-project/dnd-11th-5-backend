package com.odiga.fiesta.festival.dto.request;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FestivalCreateRequest {

	private String name;
	private String description;
	private String startDate;
	private String endDate;
	private String address;
	private String sido;
	private String sigungu;
	private String playtime;
	private String homepageUrl;
	private String instagramUrl;
	private String fee;
	private List<Long> categoryIds;
	private List<Long> moodIds;
	private String tip;
}
