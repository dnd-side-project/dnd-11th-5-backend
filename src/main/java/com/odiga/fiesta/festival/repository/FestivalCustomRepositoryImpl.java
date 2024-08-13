package com.odiga.fiesta.festival.repository;

import static com.odiga.fiesta.festival.domain.QFestival.*;
import static com.odiga.fiesta.festival.domain.QFestivalBookmark.*;
import static com.odiga.fiesta.sido.domain.QSido.*;
import static java.util.Objects.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.dto.projection.FestivalWithBookmark;
import com.odiga.fiesta.festival.dto.projection.FestivalWithBookmarkAndSido;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FestivalCustomRepositoryImpl implements FestivalCustomRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<Festival> findFestivalsWithinDateRange(LocalDate startDate, LocalDate endDate) {
		return queryFactory
			.select(festival)
			.from(festival)
			.where(festival.startDate.between(startDate, endDate)
				.or(festival.endDate.between(startDate, endDate))
				.or(festival.startDate.loe(startDate).and(festival.endDate.goe(endDate)))
			).fetch();
	}

	@Override
	public Page<FestivalWithBookmarkAndSido> findFestivalsInDate(LocalDate date, Pageable pageable,
		Long userId) {

		List<FestivalWithBookmarkAndSido> festivals =
			selectFestivalsWithBookmarkAndSido(pageable, userId)
				.where(getDateRangeCondition(date))
				.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(festival.count())
			.from(festival)
			.where(getDateRangeCondition(date));

		return PageableExecutionUtils.getPage(festivals, pageable, countQuery::fetchOne);
	}

	private JPAQuery<FestivalWithBookmarkAndSido> selectFestivalsWithBookmarkAndSido(Pageable pageable,
		Long userId) {
		return queryFactory.select(
				Projections.fields(FestivalWithBookmarkAndSido.class,
					festival.id.as("festivalId"),
					festival.name,
					festival.sigungu,
					festival.startDate,
					festival.endDate,
					new CaseBuilder()
						.when(festivalBookmarkUserIdEq(userId))
						.then(true)
						.otherwise(false).as("isBookMarked"),
					sido.name.as("sido")
				)
			).from(festival)
			.leftJoin(festivalBookmark)
			.on(festivalBookmark.festivalId.eq(festival.id),
				festivalBookmarkUserIdEq(userId))
			.leftJoin(sido)
			.on(sido.id.eq(festival.sidoId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());
	}

	private JPAQuery<FestivalWithBookmark> selectFestivalWithBookmark(Long userId) {

		return queryFactory.select(
				Projections.fields(FestivalWithBookmark.class,
					festival.id.as("festivalId"),
					festival.name,
					festival.sigungu,
					festival.startDate,
					festival.endDate,
					new CaseBuilder()
						.when(festivalBookmarkUserIdEq(userId))
						.then(false)
						.otherwise(true).as("isBookMarked")
				)
			).from(festival)
			.leftJoin(festivalBookmark)
			.on(festivalBookmark.festivalId.eq(festival.id),
				festivalBookmarkUserIdEq(userId));
	}

	private BooleanExpression festivalBookmarkUserIdEq(Long userId) {
		if (isNull(userId)) { // 항상 false
			return Expressions.asBoolean(false).isTrue();
		}

		return festivalBookmark.userId.eq(userId);
	}

	private BooleanExpression getDateRangeCondition(LocalDate date) {
		return festival.startDate.loe(date).and(festival.endDate.goe(date));
	}
}

