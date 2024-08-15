package com.odiga.fiesta.festival.controller;

import static com.odiga.fiesta.common.error.ErrorCode.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Clock;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import com.odiga.fiesta.ControllerTestSupport;
import com.odiga.fiesta.festival.dto.response.FestivalInfoResponse;
import com.odiga.fiesta.festival.dto.response.FestivalMonthlyResponse;
import com.odiga.fiesta.festival.service.FestivalService;

class FestivalControllerTest extends ControllerTestSupport {

	private final String PREFIX = "/api/v1/festivals";

	@Autowired
	private FestivalService festivalService;

	@DisplayName("페스티벌 월간 조회 - 유효하지 않은 월 값이 들어오면 에러가 발생한다.")
	@Test
	void getMonthlyFestivals_invalidParameter() throws Exception {
		// given // when // then
		mockMvc.perform(get(PREFIX + "/monthly")
				.param("year", "2023")
				.param("month", "13"))
			.andExpect(status().isBadRequest());
	}

	@DisplayName("페스티벌 월간 조회 - 특정 년, 월의 페스티벌들을 조회한다.")
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

	@DisplayName("페스티벌 일간 조회 - 특정 일에 진행되고 있는 페스티벌을 조회한다.")
	@Test
	void getFestivalsByDay() throws Exception {
		// given
		Pageable pageable = PageRequest.of(0, 6);
		PageImpl<FestivalInfoResponse> page =
			new PageImpl<>(List.of(FestivalInfoResponse.builder().build()), pageable, 1);

		when(festivalService.getFestivalsByDay(null, 2023, 10, 4, pageable)).thenReturn(page);

		// when // then
		mockMvc.perform(get(PREFIX + "/daily")
				.param("year", "2023")
				.param("month", "10")
				.param("day", "4")
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("페스티벌 일간 조회 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());
	}

	@DisplayName("페스티벌 일간 조회 - 유효하지 않는 날짜 값이 들어오면 에러가 발생한다.")
	@Test
	void getFestivalsByDay_invalidParameter() throws Exception {
		// given // when // then
		mockMvc.perform(get(PREFIX + "/daily")
				.param("year", "2023")
				.param("month", "11")
				.param("day", "43")
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value(INVALID_FESTIVAL_DATE.getMessage()));
	}

	// @DisplayName("페스티벌 필터 조회 - 필터를 이용해 페스티벌을 조회한다.")
	// @Test
	// void getFestivalsByFilters() throws Exception {
	// 	// given
	// 	Pageable pageable = PageRequest.of(0, 6, Sort.by("startDate").ascending());
	//
	// 	List<FestivalInfoResponse> responseList = List.of(FestivalInfoResponse.builder().build());
	// 	PageImpl<FestivalInfoResponse> festivalsPage = new PageImpl<>(responseList, pageable, responseList.size());
	//
	// 	FestivalFilterRequest festivalFilterRequest = FestivalFilterRequest.builder()
	// 		.areas(List.of())
	// 		.months(List.of())
	// 		.categories(List.of())
	// 		.build();
	//
	// 	System.out.println("??? " + festivalFilterRequest);
	//
	// 	final Clock CURRENT_DATE = Clock.fixed(Instant.parse("2024-10-04T10:00:00Z"), ZoneOffset.UTC);
	//
	// 	doReturn(Instant.now(CURRENT_DATE))
	// 		.when(clock)
	// 		.instant();
	//
	// 	when(festivalService.getFestivalByFiltersAndSort(null, eq(festivalFilterRequest), null, null, pageable))
	// 		.thenReturn(festivalsPage);
	//
	// 	// when // then
	// 	mockMvc.perform(get(PREFIX + "/filter")
	// 			.param("areas", "")
	// 			.param("months", "")
	// 			.param("categories", "")
	// 		)
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.message").value("페스티벌 필터 조회 성공"))
	// 		.andExpect(jsonPath("$.data").isNotEmpty());
	// }

	@DisplayName("페스티벌 필터 조회 - 정렬 조건이 dist 일 떄, 현재 위도, 경도 값이 들어오지 않으면 에러가 발생한다.")
	@Test
	void getFestivalsByFilters_InvalidLocation() throws Exception {
		// given // when // then
		mockMvc.perform(get(PREFIX + "/filter")
				.param("areas", "1,3")
				.param("months", "1,2,3,4,5")
				.param("categories", "")
				.param("lat", "")
				.param("lng", "")
				.param("sort", "dist")
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value(INVALID_CURRENT_LOCATION.getMessage()));
	}
}
