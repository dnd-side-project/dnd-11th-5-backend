package com.odiga.fiesta.festival.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.odiga.fiesta.MockTestSupport;
import com.odiga.fiesta.category.domain.Category;
import com.odiga.fiesta.category.repository.CategoryRepository;
import com.odiga.fiesta.festival.dto.response.CategoryResponse;

class CategoryServiceTest extends MockTestSupport {

	@InjectMocks
	private CategoryService categoryService;

	@Mock
	private CategoryRepository categoryRepository;

	@DisplayName("모든 카테고리를 반환한다.")
	@Test
	void getAllCategories() {
		// given
		List<Category> categories = List.of(
			Category.builder().id(1L).category("문화").build(),
			Category.builder().id(2L).category("영화").build()
		);

		given(categoryRepository.findAll())
			.willReturn(categories);

		// when
		final List<CategoryResponse> actual = categoryService.getAllCategories();

		// then
		assertThat(actual).usingRecursiveComparison()
			.isEqualTo(categories.stream().map(CategoryResponse::of).toList());
	}

}
