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
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.domain.QFestivalBookmark;
import com.odiga.fiesta.festival.dto.projection.FestivalDetailData;
import com.odiga.fiesta.festival.dto.projection.FestivalWithBookmark;
import com.odiga.fiesta.festival.dto.projection.FestivalWithBookmarkAndSido;
import com.odiga.fiesta.festival.dto.projection.FestivalWithBookmarkCountAndSido;
import com.odiga.fiesta.festival.dto.projection.FestivalWithSido;
import com.odiga.fiesta.festival.dto.request.FestivalFilterCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class FestivalCustomRepositoryImpl implements FestivalCustomRepository {

	private final JPAQueryFactory queryFactory;
	private final QFestivalBookmark festivalBookmarkForBookmarkCount = new QFestivalBookmark(
		"festivalBookmarkForBookmarkCount");
	private final QFestivalBookmark festivalBookmarkForIsBookmarked = new QFestivalBookmark(
		"festivalBookmarkForIsBookmarked");

	@Override
	public List<Festival> findFestivalsWithinDateRange(LocalDate startDate, LocalDate endDate) {
		return queryFactory
			.select(festival)
			.from(festival)
			.where(getDateBetweenCondition(startDate, endDate))
			.fetch();
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
	public Page<FestivalWithSido> findFestivalsAndSidoWithinDateRange(LocalDate startDate, LocalDate endDate,
		Pageable pageable) {
		List<FestivalWithSido> festivals = selectFestivalWithSido()
			.where(getDateBetweenCondition(startDate, endDate))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(festival.startDate.asc())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(festival.count())
			.from(festival)
			.where(getDateBetweenCondition(startDate, endDate));

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

	@Override
	public Page<FestivalWithSido> findMostLikeFestival(Pageable pageable) {

		StringPath bookmarkCount = Expressions.stringPath("bookmarkCount");

		List<FestivalWithSido> festivals =
			queryFactory.select(
					Projections.fields(
						FestivalWithBookmarkCountAndSido.class,
						festival.id.as("festivalId"),
						festival.name,
						festival.sigungu,
						sido.name.as("sido"),
						festival.startDate,
						festival.endDate,
						ExpressionUtils.as(
							JPAExpressions
								.select(festivalBookmark.id.countDistinct())
								.from(festivalBookmark)
								.where(festivalBookmark.festivalId.eq(festival.id)),
							"bookmarkCount"
						)
					)
				)
				.from(festival)
				.leftJoin(sido)
				.on(festival.sidoId.eq(sido.id))
				.where(
					festival.isPending.isFalse()
				)
				.orderBy(bookmarkCount.desc())
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch()
				.stream().map(FestivalWithSido::of)
				.toList();

		JPAQuery<Long> countQuery = queryFactory
			.select(festival.count())
			.from(festival);

		return PageableExecutionUtils.getPage(festivals, pageable, countQuery::fetchOne);
	}

	@Override
	public Optional<FestivalDetailData> findFestivalDetail(Long userId, Long festivalId) {
		FestivalDetailData festivalDetailData = queryFactory.select(
				Projections.fields(
					FestivalDetailData.class,
					festival.id.as("festivalId"),
					festival.name,
					sido.name.as("sido"),
					festival.sigungu,
					festival.startDate,
					festival.endDate,
					festival.description,
					festival.address,
					festival.tip,
					festival.homepageUrl,
					festival.instagramUrl,
					festival.latitude,
					festival.longitude,
					festival.fee,
					festival.ticketLink,
					festivalBookmarkForBookmarkCount.id.countDistinct().as("bookmarkCount"),
					new CaseBuilder()
						.when(festivalBookmarkUserIdEq(userId))
						.then(true)
						.otherwise(false).as("isBookmarked")
				)
			)
			.from(festival)
			.leftJoin(festivalBookmarkForIsBookmarked)
			.on(festivalBookmarkForIsBookmarked.festivalId.eq(festival.id), festivalBookmarkUserIdEq(userId))
			.leftJoin(festivalBookmarkForBookmarkCount)
			.on(festivalBookmarkForBookmarkCount.festivalId.eq(festival.id))
			.leftJoin(sido)
			.on(festival.sidoId.eq(sido.id))
			.leftJoin(festivalBookmark)
			.on(festival.id.eq(festivalBookmark.festivalId))
			.where(
				festival.id.eq(festivalId),
				festival.isPending.isFalse()
			)
			.fetchOne();

		return Optional.ofNullable(festivalDetailData);
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
			.leftJoin(festivalBookmarkForIsBookmarked)
			.on(festivalBookmarkForIsBookmarked.festivalId.eq(festival.id),
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

	private JPAQuery<FestivalWithSido> selectFestivalWithSido() {
		return queryFactory.select(
				Projections.fields(FestivalWithSido.class,
					festival.id.as("festivalId"),
					festival.name,
					sido.name.as("sido"),
					festival.sigungu,
					festival.startDate,
					festival.endDate
				)
			).from(festival)
			.leftJoin(sido)
			.on(festival.sidoId.eq(sido.id));
	}

	private static BooleanExpression getOngoingFestivalCondition(LocalDate date) {
		return festival.endDate.goe(Expressions.asDate(date));
	}

	private BooleanExpression festivalBookmarkUserIdEq(Long userId) {
		if (isNull(userId)) { // 항상 false
			return Expressions.asBoolean(false).isTrue();
		}

		return festivalBookmarkForIsBookmarked.userId.eq(userId);
	}

	private BooleanExpression getDateRangeCondition(LocalDate date) {
		return festival.startDate.loe(date).and(festival.endDate.goe(date));
	}

	private BooleanExpression getDateBetweenCondition(LocalDate startDate, LocalDate endDate) {
		return festival.startDate.between(startDate, endDate)
			.or(festival.endDate.between(startDate, endDate))
			.or(festival.startDate.loe(startDate).and(festival.endDate.goe(endDate)));
	}

	private BooleanExpression festivalNameContains(String name) {
		return festival.name.contains(name);
	}
}

