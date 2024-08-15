package com.odiga.fiesta.festival.repository;

import static com.odiga.fiesta.category.domain.QCategory.*;
import static com.odiga.fiesta.festival.domain.QFestival.*;
import static com.odiga.fiesta.festival.domain.QFestivalBookmark.*;
import static com.odiga.fiesta.sido.domain.QSido.*;
import static java.util.Objects.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.dto.projection.FestivalWithBookmark;
import com.odiga.fiesta.festival.dto.projection.FestivalWithBookmarkAndSido;
import com.odiga.fiesta.festival.dto.request.FestivalFilterCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
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

	@Override
	public Page<FestivalWithBookmarkAndSido> findFestivalsByFiltersAndSort(Long userId,
		FestivalFilterCondition festivalFilterCondition,
		Double latitude, Double longitude, LocalDate date, Pageable pageable) {

		BooleanBuilder filterCondition = getFilterCondition(festivalFilterCondition);

		// endDate 가 현재 날짜보다 크거나 같아야 한다.
		List<FestivalWithBookmarkAndSido> festivals =
			selectFestivalsWithBookmarkAndSido(pageable, userId)
				.where(filterCondition.and(getOngoingFestivalCondition(date)))
				.orderBy(getAllOrderSpecifiers(pageable, latitude, longitude).toArray(OrderSpecifier[]::new))
				.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(festival.count())
			.from(festival)
			.where(filterCondition);

		return PageableExecutionUtils.getPage(festivals, pageable, countQuery::fetchOne);
	}

	@Override
	public Page<FestivalWithBookmarkAndSido> findFestivalsByQuery(Long userId, String query, Pageable pageable) {
		List<FestivalWithBookmarkAndSido> festivals =
			selectFestivalsWithBookmarkAndSido(pageable, userId)
				.where(festivalNameContains(query))
				.orderBy(festival.startDate.asc())
				.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(festival.count())
			.from(festival)
			.where(festivalNameContains(query));

		return PageableExecutionUtils.getPage(festivals, pageable, countQuery::fetchOne);
	}

	private List<OrderSpecifier> getAllOrderSpecifiers(Pageable pageable, Double latitude, Double longitude) {
		List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

		if (pageable.getSort().isEmpty()) {
			return orderSpecifiers;
		}

		for (Sort.Order order : pageable.getSort()) {
			String property = order.getProperty();

			// 정렬 조건이 dist 인 경우, 위도/경도 값을 받아서 새로운 specifier 를 만든다.
			if ("dist".equals(property)) {
				OrderSpecifier distSpecifier = FestivalSortType.getDistanceOrderSpecifier(
					order.getDirection().isAscending() ?
						Order.ASC : Order.DESC, latitude, longitude);

				orderSpecifiers.add(distSpecifier);
			} else {
				OrderSpecifier specifier = FestivalSortType.getFestivalSortType(order.getProperty())
					.getOrderSpecifier(order.getDirection().isAscending() ? Order.ASC : Order.DESC);

				orderSpecifiers.add(specifier);
			}

		}

		return orderSpecifiers;
	}

	private BooleanBuilder getFilterCondition(FestivalFilterCondition festivalFilterCondition) {
		BooleanBuilder filterCondition = new BooleanBuilder();

		// 지역 필터
		BooleanBuilder areaCondition = new BooleanBuilder();
		festivalFilterCondition.getAreas().forEach(areaId -> areaCondition.or(festival.sidoId.eq(areaId)));

		// 월간 필터
		BooleanBuilder monthCondition = new BooleanBuilder();
		festivalFilterCondition.getMonths().forEach(month -> {
			BooleanBuilder periodCondition = new BooleanBuilder();

			YearMonth yearMonth = YearMonth.of(LocalDate.now().getYear(), month);
			LocalDate monthStart = yearMonth.atDay(1);
			LocalDate monthEnd = yearMonth.atEndOfMonth();

			periodCondition
				.and(festival.endDate.goe(monthStart))
				.and(festival.startDate.loe(monthEnd));

			monthCondition.or(periodCondition);
		});

		// 카테고리 필터
		BooleanBuilder categoryCondition = new BooleanBuilder();
		festivalFilterCondition.getCategories().forEach(categoryId -> categoryCondition.or(category.id.eq(categoryId)));

		// 값이 없는 경우 and 연산하면 stackoverflow
		if (areaCondition.hasValue()) {
			filterCondition.and(areaCondition);
		}

		if (monthCondition.hasValue()) {
			filterCondition.and(monthCondition);
		}

		if (categoryCondition.hasValue()) {
			filterCondition.and(monthCondition);
		}

		return filterCondition;
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

	private static BooleanExpression getOngoingFestivalCondition(LocalDate date) {
		return festival.endDate.goe(Expressions.asDate(date));
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

	private BooleanExpression festivalNameContains(String name) {
		return festival.name.contains(name);
	}
}

