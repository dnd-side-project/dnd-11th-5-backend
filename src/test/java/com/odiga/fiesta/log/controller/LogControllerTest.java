package com.odiga.fiesta.log.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.odiga.fiesta.ControllerTestSupport;
import com.odiga.fiesta.common.error.ErrorCode;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.log.dto.response.LogDetailResponse;
import com.odiga.fiesta.log.dto.response.LogKeywordResponse;
import com.odiga.fiesta.log.service.LogService;

class LogControllerTest extends ControllerTestSupport {

	@Autowired
	private LogService logService;

	@DisplayName("방문일지 키워드들을 조회한다.")
	@Test
	void getAllLogKeywords() throws Exception {
		// given
		String message = "방문일지 키워드 조회 성공";

		List<LogKeywordResponse> mockLogKeywords = List.of();
		when(logService.getAllLogKeywords()).thenReturn(mockLogKeywords);

		// when // then
		mockMvc.perform(
				get("/api/v1/logs/keywords")
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value(message))
			.andExpect(jsonPath("$.data").isArray());
	}

	@DisplayName("방문일지 ID를 이용해 방문일지를 상세 조회한다.")
	@Test
	void getLogDetail() throws Exception {
		// given
		String message = "방문일지 상세 조회 성공";

		LogDetailResponse logDetailResponse = LogDetailResponse.builder().build();
		Long logId = 1L;

		when(logService.getLogDetail(logId)).thenReturn(logDetailResponse);

		// when // then
		mockMvc.perform(
				get("/api/v1/logs/" + logId)
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value(message));
	}

	@DisplayName("존재하지 않는 방문일지 ID로 조회하면 404 에러가 발생한다.")
	@Test
	void getLogDetail_NotFound() throws Exception {
		// given
		Long logId = 17L;
		CustomException logNotFoundException = new CustomException(ErrorCode.LOG_NOT_FOUND);
		when(logService.getLogDetail(logId)).thenThrow(logNotFoundException);

		// when // then
		mockMvc.perform(
				get("/api/v1/logs/" + logId)
			).andDo(print())
			.andExpect(status().isNotFound());
	}
}
