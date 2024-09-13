package com.odiga.fiesta.festival.dto.response;

import java.time.LocalDate;

import com.odiga.fiesta.festival.dto.projection.FestivalWithBookmarkAndSido;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class FestivalInfoWithBookmark extends FestivalInfo {

	private Boolean isBookmarked;

	public FestivalInfoWithBookmark(Long festivalId, String name, String sido, String sigungu, String thumbnailImage,
		LocalDate startDate, LocalDate endDate, Boolean isBookmarked) {
		super(festivalId, name, sido, sigungu, thumbnailImage, startDate, endDate);
		this.isBookmarked = isBookmarked;
	}

	public static FestivalInfoWithBookmark of(FestivalWithBookmarkAndSido festival, String thumbnailImage) {
		return FestivalInfoWithBookmark.builder()
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
