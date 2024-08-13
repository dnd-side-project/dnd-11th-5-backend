package com.odiga.fiesta.festival.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.odiga.fiesta.ControllerTestSupport;
import com.odiga.fiesta.festival.dto.response.FestivalMonthlyResponse;
import com.odiga.fiesta.festival.service.FestivalService;

class FestivalControllerTest extends ControllerTestSupport {

	private final String PREFIX = "/api/v1/festivals";

	@Autowired
	private FestivalService festivalService;

	@DisplayName("유효하지 않은 값의 month 값이 들어오면 에러가 발생한다.")
	@Test
	void getMonthlyFestivals_invalidParameter() throws Exception {
		// given // when // then
		mockMvc.perform(get(PREFIX + "/monthly")
				.param("year", "2023")
				.param("month", "13"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("C003"));
	}

	@DisplayName("특정 년, 월의 페스티벌들을 조회한다.")
	@Test
	void getMonthlyFestivals() throws Exception {
		// Given
		FestivalMonthlyResponse mockResponse = FestivalMonthlyResponse.builder().build();
		when(festivalService.getMonthlyFestivals(2024, 10)).thenReturn(mockResponse);

		// When & Then
		mockMvc.perform(get(PREFIX + "/monthly")
				.param("year", "2024")
				.param("month", "10")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("페스티벌 월간 조회 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());
	}

	@DisplayName("유효하지 않는 날짜 값이 들어오면 에러가 발생한다.")
	@Test
	void getFestivalsByDay_invalidParameter() throws Exception {
		// given // when // then
		mockMvc.perform(get(PREFIX + "/daily")
				.param("year", "2023")
				.param("month", "11")
				.param("day", "43")
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("유효하지 않은 날짜입니다."));
	}
}
