package com.odiga.fiesta.review.repository;

import static com.odiga.fiesta.festival.domain.QFestival.*;
import static com.odiga.fiesta.festival.domain.QFestivalCategory.*;
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
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.odiga.fiesta.review.domain.QReviewLike;
import com.odiga.fiesta.review.dto.projection.ReviewDataWithLike;
import com.odiga.fiesta.review.dto.projection.ReviewSimpleData;
import com.odiga.fiesta.review.dto.response.ReviewImageResponse;
import com.odiga.fiesta.review.dto.response.ReviewKeywordResponse;
import com.odiga.fiesta.review.dto.response.ReviewUserInfo;
import com.odiga.fiesta.review.dto.response.TopReviewKeywordResponse;
import com.odiga.fiesta.review.dto.response.TopReviewKeywordsResponse;
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
	public Optional<ReviewDataWithLike> findReview(Long userId, Long reviewId) {
		ReviewDataWithLike data = getDataWithLikeJPAQuery(userId)
			.where(review.id.eq(reviewId))
			.fetchOne();

		return Optional.ofNullable(data);
	}


	@Override
	public Page<ReviewDataWithLike> findReviews(Long userId, Long festivalId, Pageable pageable) {
		List<ReviewDataWithLike> content = getDataWithLikeJPAQuery(userId)
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
	public List<ReviewSimpleData> findMostLikeReviews(Long size) {
		return queryFactory.select(
				Projections.fields(
					ReviewSimpleData.class,
					review.id.as("reviewId"),
					review.festivalId,
					festival.name.as("festivalName"),
					review.content,
					review.rating
				)
			).from(review)
			.leftJoin(reviewLike)
			.on(reviewLike.reviewId.eq(review.id))
			.leftJoin(festival)
			.on(review.festivalId.eq(festival.id))
			.groupBy(review.id)
			.orderBy(ReviewSortType.getReviewSortType("likeCount").getOrderSpecifier(DESC))
			.limit(size)
			.fetch();
	}

	@Override
	public TopReviewKeywordsResponse findTopReviewKeywords(Long festivalId, Long size) {
		List<TopReviewKeywordResponse> keywords = queryFactory
			.select(
				Projections.fields(
					TopReviewKeywordResponse.class,
					keyword.id.as("keywordId"),
					keyword.content.as("keyword"),
					reviewKeyword.id.countDistinct().as("selectionCount")
				)
			)
			.from(review)
			.where(review.festivalId.eq(festivalId)
				.and(reviewKeyword.id.isNotNull())
				.and(keyword.id.isNotNull()))
			.leftJoin(reviewKeyword)
			.on(review.id.eq(reviewKeyword.reviewId))
			.leftJoin(keyword)
			.on(reviewKeyword.keywordId.eq(keyword.id))
			.orderBy(reviewKeyword.id.countDistinct().desc(), reviewKeyword.createdAt.max().desc())
			.groupBy(keyword.id)
			.limit(size)
			.fetch();

		Long totalCount = queryFactory
			.select(reviewKeyword.id.countDistinct())
			.from(review)
			.leftJoin(reviewKeyword)
			.on(review.id.eq(reviewKeyword.reviewId))
			.where(review.festivalId.eq(festivalId))
			.fetchOne();

		return TopReviewKeywordsResponse.builder()
			.keywords(keywords)
			.totalCount(totalCount)
			.build();
	}

	@Override
	public Long countByUserIdAndCategoryId(Long userId, Long festivalCategoryId) {
		return queryFactory
			.select(review.id.countDistinct())
			.from(review)
			.leftJoin(festival)
			.on(review.festivalId.eq(festival.id))
			.leftJoin(festivalCategory)
			.on(festivalCategory.festivalId.eq(festival.id))
			.where(review.userId.eq(userId)
				.and(festivalCategory.categoryId.eq(festivalCategoryId)))
			.fetchOne();
	}

	@Override
	public Map<Long, List<ReviewKeywordResponse>> findReviewKeywordsMap(List<Long> reviewIds) {
		Map<Long, List<ReviewKeywordResponse>> resultMap = queryFactory
			.from(reviewKeyword)
			.leftJoin(keyword).on(reviewKeyword.keywordId.eq(keyword.id))
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
			.leftJoin(review).on(review.id.eq(reviewImage.reviewId))
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

	private JPAQuery<ReviewDataWithLike> getDataWithLikeJPAQuery(Long userId) {
		return queryFactory.select(
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
			).from(review)
			.leftJoin(reviewLike)
			.on(reviewLike.reviewId.eq(review.id))
			.leftJoin(reviewLikeForIsLiked)
			.on(reviewLikeForIsLiked.reviewId.eq(review.id), reviewLikeUserIdEq(userId))
			.leftJoin(user)
			.on(review.userId.eq(user.id));
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
