package com.odiga.fiesta.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopReviewKeywordResponse {

	private Long keywordId;
	private String keyword;
	private Long selectionCount;
}
