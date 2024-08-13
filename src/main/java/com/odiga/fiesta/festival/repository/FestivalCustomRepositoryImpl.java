package com.odiga.fiesta.festival.repository;

import static com.odiga.fiesta.festival.domain.QFestival.*;

import java.time.LocalDate;
import java.util.List;

import com.odiga.fiesta.festival.domain.Festival;
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
}
