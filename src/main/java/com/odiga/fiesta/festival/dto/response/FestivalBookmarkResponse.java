package com.odiga.fiesta.festival.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FestivalBookmarkResponse {

	private Long festivalId;
	private Long bookmarkCount;
	private Boolean isBookmarked;
}
