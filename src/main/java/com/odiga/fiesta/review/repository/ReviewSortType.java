package com.odiga.fiesta.review.repository;

import static com.odiga.fiesta.common.error.ErrorCode.*;
import static com.odiga.fiesta.review.domain.QReview.*;
import static com.odiga.fiesta.review.domain.QReviewLike.*;

import java.util.Arrays;

import com.odiga.fiesta.common.error.exception.CustomException;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ReviewSortType {
	CREATED_AT("createdAt", review.createdAt),
	LIKE_COUNT("likeCount", reviewLike.id.countDistinct());

	private final String property;
	private final Expression target;

	public OrderSpecifier<?> getOrderSpecifier(Order direction) {
		return new OrderSpecifier(direction, this.target);
	}

	public static ReviewSortType getReviewSortType(String property) {
		return Arrays.stream(ReviewSortType.values())
			.filter(commentSortType -> commentSortType.property.equals(property))
			.findAny()
			.orElseThrow(() -> {
				throw new CustomException(INVALID_REVIEW_SORT_TYPE);
			});
	}
}
