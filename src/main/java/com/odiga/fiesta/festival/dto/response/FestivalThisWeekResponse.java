package com.odiga.fiesta.festival.dto.response;

import java.time.LocalDate;

import com.odiga.fiesta.festival.dto.projection.FestivalWithSido;

import lombok.Builder;

public class FestivalThisWeekResponse extends FestivalBasicResponse {

	private String sido;
	private String sigungu;
	private String thumbnailImage;
	private LocalDate startDate;
	private LocalDate endDate;

	@Builder
	public FestivalThisWeekResponse(Long festivalId, String name, Long festivalId1, String name1, String sido,
		String sigungu, String thumbnailImage, LocalDate startDate, LocalDate endDate) {
		super(festivalId, name);
		this.sido = sido;
		this.sigungu = sigungu;
		this.thumbnailImage = thumbnailImage;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public static FestivalThisWeekResponse of(FestivalWithSido festival, String thumbnailImageUrl) {
		return FestivalThisWeekResponse.builder()
			.festivalId(festival.getFestivalId())
			.name(festival.getName())
			.sido(festival.getSido())
			.sigungu(festival.getSigungu())
			.thumbnailImage(thumbnailImageUrl)
			.startDate(festival.getStartDate())
			.endDate(festival.getEndDate())
			.build();

	}

}
