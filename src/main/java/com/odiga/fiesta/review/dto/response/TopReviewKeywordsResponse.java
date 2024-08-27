package com.odiga.fiesta.review.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopReviewKeywordsResponse {

	private List<TopReviewKeywordResponse> keywords;
	private Long totalCount;
}
