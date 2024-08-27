package com.odiga.fiesta.review.repository;

import static com.odiga.fiesta.keyword.domain.QKeyword.*;
import static com.odiga.fiesta.review.domain.QReview.*;
import static com.odiga.fiesta.review.domain.QReviewImage.*;
import static com.odiga.fiesta.review.domain.QReviewKeyword.*;
import static com.odiga.fiesta.review.domain.QReviewLike.*;
import static com.odiga.fiesta.user.domain.QUser.*;
import static com.querydsl.core.group.GroupBy.*;
import static com.querydsl.core.types.Order.*;
import static java.util.Objects.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.odiga.fiesta.review.domain.QReviewLike;
import com.odiga.fiesta.review.dto.projection.ReviewDataWithLike;
import com.odiga.fiesta.review.dto.response.ReviewImageResponse;
import com.odiga.fiesta.review.dto.response.ReviewKeywordResponse;
import com.odiga.fiesta.review.dto.response.ReviewUserInfo;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {

	private final JPAQueryFactory queryFactory;
	private final QReviewLike reviewLikeForIsLiked = new QReviewLike("reviewLikeForIsLiked");

	@Override
	public Page<ReviewDataWithLike> findReviews(Long userId, Long festivalId, Pageable pageable) {
		List<ReviewDataWithLike> content = queryFactory.select(
				Projections.fields(
					ReviewDataWithLike.class,
					review.id.as("reviewId"),
					review.festivalId,
					Projections.fields(
						ReviewUserInfo.class,
						review.userId,
						user.profileImage,
						user.nickname
					).as("user"),
					review.content,
					review.createdAt,
					review.rating,
					new CaseBuilder()
						.when(reviewLikeUserIdEq(userId))
						.then(true)
						.otherwise(false).as("isLiked"),
					reviewLike.id.countDistinct().as("likeCount")
				)
			)
			.from(review)
			.leftJoin(reviewLike)
			.on(reviewLike.reviewId.eq(review.id))
			.leftJoin(reviewLikeForIsLiked)
			.on(reviewLikeForIsLiked.reviewId.eq(review.id), reviewLikeUserIdEq(userId))
			.leftJoin(user)
			.on(review.userId.eq(user.id))
			.where(reviewFestivalEq(festivalId))
			.orderBy(getAllOrderSpecifiers(pageable).toArray(OrderSpecifier[]::new))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.groupBy(review.id)
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(review.id.count())
			.from(review)
			.where(reviewFestivalEq(festivalId));

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	@Override
	public Map<Long, List<ReviewKeywordResponse>> findReviewKeywordsMap(List<Long> reviewIds) {
		Map<Long, List<ReviewKeywordResponse>> resultMap = queryFactory
			.from(reviewKeyword)
			.join(keyword).on(reviewKeyword.keywordId.eq(keyword.id))
			.where(reviewKeyword.reviewId.in(reviewIds))
			.transform(
				groupBy(reviewKeyword.reviewId)
					.as(list(Projections.fields(
						ReviewKeywordResponse.class,
						reviewKeyword.keywordId,
						keyword.content.as("keyword")
					)))
			);

		reviewIds.forEach(reviewId -> resultMap.putIfAbsent(reviewId, new ArrayList<>()));

		return resultMap;

	}

	@Override
	public Map<Long, List<ReviewImageResponse>> findReviewImagesMap(List<Long> reviewIds) {
		Map<Long, List<ReviewImageResponse>> resultMap = queryFactory
			.from(reviewImage)
			.join(review).on(review.id.eq(reviewImage.reviewId))
			.where(reviewImage.reviewId.in(reviewIds))
			.transform(
				groupBy(reviewImage.reviewId)
					.as(list(Projections.fields(
						ReviewImageResponse.class,
						reviewImage.id.as("imageId"),
						reviewImage.imageUrl
					)))
			);

		reviewIds.forEach(reviewId -> resultMap.putIfAbsent(reviewId, new ArrayList<>()));
		return resultMap;
	}

	private static BooleanExpression reviewFestivalEq(Long festivalId) {
		return review.festivalId.eq(festivalId);
	}

	private List<OrderSpecifier> getAllOrderSpecifiers(Pageable pageable) {
		if (pageable.getSort().isEmpty()) {
			return Collections.emptyList();
		}

		return pageable.getSort().stream()
			.map(order -> ReviewSortType.getReviewSortType(order.getProperty())
				.getOrderSpecifier(order.getDirection().isAscending() ? ASC : DESC))
			.collect(Collectors.toList());
	}

	private BooleanExpression reviewLikeUserIdEq(Long userId) {
		if (isNull(userId)) {
			return Expressions.asBoolean(true).isFalse();
		}

		return reviewLikeForIsLiked.userId.eq(userId);
	}
}
