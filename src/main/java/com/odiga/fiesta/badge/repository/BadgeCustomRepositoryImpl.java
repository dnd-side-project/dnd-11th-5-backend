package com.odiga.fiesta.badge.repository;

import static com.odiga.fiesta.badge.domain.QBadge.*;
import static com.odiga.fiesta.badge.domain.QUserBadge.*;

import java.util.List;

import com.odiga.fiesta.user.dto.response.UserBadgeResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BadgeCustomRepositoryImpl implements BadgeCustomRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<UserBadgeResponse> findUserBadges(Long userId) {

		return queryFactory.select(
				Projections.fields(
					UserBadgeResponse.class,
					badge.id.as("badgeId"),
					badge.name.as("badgeName"),
					badge.description,
					badge.imageUrl,
					new CaseBuilder()
						.when(userBadge.isNull()) // userBadge가 null이면 false
						.then(false)
						.when(userBadge.userId.isNull())
						.then(false)
						.when(userBadge.userId.eq(userId))
						.then(true)
						.otherwise(false).as("isAcquired")
				)
			)
			.from(badge)
			.leftJoin(userBadge)
			.on(badge.id.eq(userBadge.badgeId)) // 단순 조인
			.fetch();
	}
}
