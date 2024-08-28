package com.odiga.fiesta.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ReviewLikeResponse {

	private Long reviewId;
	private Long likeCount;
	private Boolean isLiked;
}
