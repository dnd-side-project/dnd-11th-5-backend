package com.odiga.fiesta.review.dto.response;

import static java.util.Objects.*;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.odiga.fiesta.review.dto.projection.ReviewSimpleData;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ReviewSimpleResponse extends ReviewIdResponse {

	private Long festivalId;
	private String festivalName;
	private String content;
	private double rating;
	private String thumbnailImage;
	private List<ReviewKeywordResponse> keywords;

	public static ReviewSimpleResponse of(ReviewSimpleData review, String thumbnailImage,
		List<ReviewKeywordResponse> keywords) {
		return ReviewSimpleResponse.builder()
			.reviewId(review.getReviewId())
			.festivalId(review.getFestivalId())
			.festivalName(review.getFestivalName())
			.content(review.getContent())
			.rating(review.getRating() / 10.0)
			.thumbnailImage(isNull(thumbnailImage) ? null : thumbnailImage)
			.keywords(keywords)
			.build();
	}
}
