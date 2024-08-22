package com.odiga.fiesta.festival.dto.response;

import java.util.List;

import com.odiga.fiesta.festival.dto.projection.FestivalDetailData;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class FestivalDetailResponse extends FestivalDetailData {

	private List<CategoryResponse> categories;
	private List<MoodResponse> moods;
	private List<FestivalImageResponse> images;

	public static FestivalDetailResponse of(FestivalDetailData festival, List<CategoryResponse> categories,
		List<MoodResponse> moods, List<FestivalImageResponse> images) {
		return FestivalDetailResponse.builder()
			.festivalId(festival.getFestivalId())
			.name(festival.getName())
			.sigungu(festival.getSigungu())
			.startDate(festival.getStartDate())
			.endDate(festival.getEndDate())
			.description(festival.getDescription())
			.playtime(festival.getPlaytime())
			.address(festival.getAddress())
			.latitude(festival.getLatitude())
			.longitude(festival.getLongitude())
			.sido(festival.getSido())
			.tip(festival.getTip())
			.homepageUrl(festival.getHomepageUrl())
			.instagramUrl(festival.getInstagramUrl())
			.fee(festival.getFee())
			.ticketLink(festival.getTicketLink())
			.bookmarkCount(festival.getBookmarkCount())
			.isBookmarked(festival.getIsBookmarked())
			.categories(categories)
			.moods(moods)
			.images(images)
			.build();
	}
}
