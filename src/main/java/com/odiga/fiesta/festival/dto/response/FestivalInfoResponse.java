package com.odiga.fiesta.festival.dto.response;

import java.time.LocalDate;

import com.odiga.fiesta.festival.dto.projection.FestivalWithBookmarkAndSido;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FestivalInfoResponse extends FestivalBasicResponse {

	private String sido; // 시도 행정 구역 이름
	private String sigungu; // 시군구 행정 구역 이름
	private String thumbnailImage;
	private LocalDate startDate;
	private LocalDate endDate;
	private Boolean isBookmarked;

	@Builder
	public FestivalInfoResponse(Long festivalId, String name, String sido, String sigungu, String thumbnailImage,
		LocalDate startDate, LocalDate endDate, Boolean isBookmarked) {
		super(festivalId, name);
		this.sido = sido;
		this.sigungu = sigungu;
		this.thumbnailImage = thumbnailImage;
		this.startDate = startDate;
		this.endDate = endDate;
		this.isBookmarked = isBookmarked;
	}

	public static FestivalInfoResponse of(FestivalWithBookmarkAndSido festival, String thumbnailImage) {
		return FestivalInfoResponse.builder()
			.festivalId(festival.getFestivalId())
			.name(festival.getName())
			.sido(festival.getSido())
			.sigungu(festival.getSigungu())
			.thumbnailImage(thumbnailImage)
			.startDate(festival.getStartDate())
			.endDate(festival.getEndDate())
			.isBookmarked(festival.getIsBookMarked())
			.build();
	}
}
