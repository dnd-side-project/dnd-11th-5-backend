package com.odiga.fiesta.review.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.odiga.fiesta.review.dto.projection.ReviewDataWithLike;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class ReviewResponse extends ReviewIdResponse {

	private Long festivalId;
	private ReviewUserInfo user;

	private String content;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Boolean isLiked;
	private Long likeCount;
	private Double rating;
	private List<ReviewImageResponse> images;
	private List<ReviewKeywordResponse> keywords;

	public static ReviewResponse of(ReviewDataWithLike reviewData, List<ReviewImageResponse> images,
		List<ReviewKeywordResponse> keywords) {
		return ReviewResponse.builder()
			.reviewId(reviewData.getReviewId())
			.festivalId(reviewData.getFestivalId())
			.user(reviewData.getUser())
			.content(reviewData.getContent())
			.createdAt(reviewData.getCreatedAt())
			.updatedAt(reviewData.getUpdatedAt())
			.isLiked(reviewData.getIsLiked())
			.likeCount(reviewData.getLikeCount())
			.rating((reviewData.getRating() / 10.0))
			.images(images)
			.keywords(keywords)
			.build();
	}
}
