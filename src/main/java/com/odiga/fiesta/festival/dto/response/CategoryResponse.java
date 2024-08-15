package com.odiga.fiesta.festival.dto.response;

import static lombok.AccessLevel.*;

import com.odiga.fiesta.category.domain.Category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class CategoryResponse {

	private final Long categoryId;
	private final String name;

	public static CategoryResponse of(final Category category) {
		return new CategoryResponse(
			category.getId(),
			category.getName()
		);
	}
}
