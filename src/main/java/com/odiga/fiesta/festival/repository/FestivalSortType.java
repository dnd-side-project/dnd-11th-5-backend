package com.odiga.fiesta.festival.repository;

import static com.odiga.fiesta.common.error.ErrorCode.*;
import static com.odiga.fiesta.festival.domain.QFestival.*;
import static com.querydsl.core.types.dsl.MathExpressions.*;

import java.util.Arrays;

import com.odiga.fiesta.common.error.exception.CustomException;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FestivalSortType {
	DATE("startDate", festival.startDate);

	private final String property;
	private final Expression target;

	public OrderSpecifier<?> getOrderSpecifier(Order direction) {
		return new OrderSpecifier(direction, this.target);
	}

	public static OrderSpecifier<Double> getDistanceOrderSpecifier(
		Order direction, double latitude, double longitude) {

		NumberExpression<Double> distanceExpression = acos(sin(radians(Expressions.constant(latitude)))
			.multiply(sin(radians(festival.latitude)))
			.add(cos(radians(Expressions.constant(latitude)))
				.multiply(cos(radians(festival.latitude)))
				.multiply(cos(radians(Expressions.constant(longitude)).subtract(radians(festival.longitude)))))
		).multiply(6371);

		return new OrderSpecifier<>(direction, distanceExpression);
	}

	public static FestivalSortType getFestivalSortType(String property) {
		return Arrays.stream(FestivalSortType.values())
			.filter(reviewSortType -> reviewSortType.property.equals(property))
			.findAny()
			.orElseThrow(() -> {
				throw new CustomException(INVALID_INPUT_VALUE);
			});
	}

}
