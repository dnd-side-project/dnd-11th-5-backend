package com.odiga.fiesta.festival.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.odiga.fiesta.ControllerTestSupport;
import com.odiga.fiesta.festival.dto.response.CategoryResponse;
import com.odiga.fiesta.festival.dto.response.CompanionResponse;
import com.odiga.fiesta.festival.dto.response.MoodResponse;
import com.odiga.fiesta.festival.dto.response.PriorityResponse;
import com.odiga.fiesta.global.service.CategoryService;
import com.odiga.fiesta.global.service.CompanionService;
import com.odiga.fiesta.global.service.MoodService;
import com.odiga.fiesta.global.service.PriorityService;

class FestivalControllerTest extends ControllerTestSupport {

	@Autowired
	private MoodService moodService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CompanionService companionService;

	@Autowired
	private PriorityService priorityService;

	@DisplayName("모든 페스티벌 분위기를 조회한다.")
	@Test
	void getAllMoods() throws Exception {
		// given
		String message = "페스티벌 분위기 조회 성공";

		List<MoodResponse> mockMoods = List.of();
		when(moodService.getAllMoods()).thenReturn(mockMoods);

		// when // then
		mockMvc.perform(
				get("/api/v1/festivals/moods")
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value(message))
			.andExpect(jsonPath("$.data").isArray());
	}

	@DisplayName("모든 카테고리를 조회한다.")
	@Test
	void getAllCategories() throws Exception {
		// given
		String message = "페스티벌 카테고리 조회 성공";

		List<CategoryResponse> mockCategories = List.of();
		when(categoryService.getAllCategories()).thenReturn(mockCategories);

		// when // then
		mockMvc.perform(
				get("/api/v1/festivals/categories")
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value(message))
			.andExpect(jsonPath("$.data").isArray());

	}

	@DisplayName("모든 일행 타입을 조회한다.")
	@Test
	void getAllCompanions() throws Exception {
		// given
		String message = "페스티벌 일행 분류 조회 성공";

		List<CompanionResponse> mockCompanions = List.of();
		when(companionService.getAllCompanions()).thenReturn(mockCompanions);

		// when // then
		mockMvc.perform(
				get("/api/v1/festivals/companions")
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value(message))
			.andExpect(jsonPath("$.data").isArray());
	}

	@DisplayName("모든 페스티벌 우선순위 타입을 조회한다.")
	@Test
	void getAllPriorities() throws Exception {
		// given
		String message = "페스티벌 우선순위 조회 성공";

		List<PriorityResponse> mockPriorities = List.of();
		when(priorityService.getAllPriorities()).thenReturn(mockPriorities);

		// when // then
		mockMvc.perform(
				get("/api/v1/festivals/priorities")
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value(message))
			.andExpect(jsonPath("$.data").isArray());
	}

}
