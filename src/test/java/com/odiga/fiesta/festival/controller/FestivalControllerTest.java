package com.odiga.fiesta.festival.controller;

import static com.odiga.fiesta.common.error.ErrorCode.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.odiga.fiesta.ControllerTestSupport;
import com.odiga.fiesta.auth.domain.AuthUser;
import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.dto.request.CreateFestivalModificationRequest;
import com.odiga.fiesta.festival.dto.response.FestivalInfoWithBookmark;
import com.odiga.fiesta.festival.dto.response.FestivalMonthlyResponse;
import com.odiga.fiesta.festival.repository.FestivalRepository;
import com.odiga.fiesta.festival.service.FestivalService;

class FestivalControllerTest extends ControllerTestSupport {

	@Autowired
	private FestivalService festivalService;

	@DisplayName("페스티벌 월간 조회 - 유효하지 않은 월 값이 들어오면 에러가 발생한다.")
	@Test
	void getMonthlyFestivals_invalidParameter() throws Exception {
		// given // when // then
		mockMvc.perform(get("/api/v1/festivals/monthly")
				.param("year", "2023")
				.param("month", "13"))
			.andExpect(status().isBadRequest());
	}

	@DisplayName("페스티벌 월간 조회 - 특정 년, 월의 페스티벌들을 조회한다.")
	@Test
	void getMonthlyFestivals() throws Exception {
		// given
		FestivalMonthlyResponse mockResponse = FestivalMonthlyResponse.builder().build();
		when(festivalService.getMonthlyFestivals(2024, 10)).thenReturn(mockResponse);

		// when // then
		mockMvc.perform(get("/api/v1/festivals/monthly")
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
		PageImpl<FestivalInfoWithBookmark> page =
			new PageImpl<>(List.of(FestivalInfoWithBookmark.builder().build()), pageable, 1);

		when(festivalService.getFestivalsByDay(1L, 2023, 10, 4, pageable)).thenReturn(page);

		// when // then
		mockMvc.perform(get("/api/v1/festivals/daily")
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
		mockMvc.perform(get("/api/v1/festivals/daily")
				.param("year", "2023")
				.param("month", "11")
				.param("day", "43")
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value(INVALID_FESTIVAL_DATE.getMessage()));
	}

	@DisplayName("페스티벌 필터 조회 - 정렬 조건이 dist 일 떄, 현재 위도, 경도 값이 들어오지 않으면 에러가 발생한다.")
	@Test
	void getFestivalsByFilters_InvalidLocation() throws Exception {
		// given // when // then
		mockMvc.perform(get("/api/v1/festivals/filter")
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

	@DisplayName("페스티벌 이름 검색 - 검색어가 비어있으면 에러가 발생한다.")
	@Test
	void getFestivalsByQuery_QueryIsNull() throws Exception {
		// given // when // then
		mockMvc.perform(get("/api/v1/festivals/search")
				.param("query", "")
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value(QUERY_CANNOT_BE_EMPTY.getMessage()));
	}

	@DisplayName("페스티벌 이름 검색 - 검색어가 공백이면 에러가 발생한다.")
	@Test
	void getFestivalsByQuery_QueryIsBlank() throws Exception {
		// given // when // then
		mockMvc.perform(get("/api/v1/festivals/search")
				.param("query", " ")
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value(QUERY_CANNOT_BE_BLANK.getMessage()));
	}

	@DisplayName("다가오는 페스티벌 조회 -로그인하지 않는 경우 에러가 발생한다.")
	@Test
	void getUpcomingFestival_NotLoggedIn() throws Exception {
		// given
		SecurityContextHolder.clearContext();

		// when // then
		mockMvc.perform(get("/api/v1/festivals/upcoming")
				.param("query", " ")
			)
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.message").value(NOT_LOGGED_IN.getMessage()));
	}

	@DisplayName("페스티벌 수정 사항 요청 - 내용이 비어있으면 에러가 발생한다.")
	@Test
	void createFestivalRequest_EmptyContent() throws Exception {
		// given
		CreateFestivalModificationRequest request = CreateFestivalModificationRequest.builder()
			.content("")
			.build();

		// when // then
		mockMvc.perform(post("/api/v1/festivals/{festivalId}/request", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}

	@DisplayName("페스티벌 수정 사항 요청 - 로그인 하지 않는 경우 에러가 발생한다.")
	@Test
	void createFestivalRequest_NotLoggedIn() throws Exception {
		// given
		SecurityContextHolder.clearContext();

		CreateFestivalModificationRequest request = CreateFestivalModificationRequest.builder()
			.content("리뷰 내용")
			.build();

		// when // then
		mockMvc.perform(post("/api/v1/festivals/{festivalId}/request", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.message").value(NOT_LOGGED_IN.getMessage()));
	}
}
