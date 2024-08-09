package com.odiga.fiesta.log.controller;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.odiga.fiesta.ControllerTestSupport;
import com.odiga.fiesta.log.dto.response.LogKeywordResponse;
import com.odiga.fiesta.log.service.LogService;

class LogControllerTest extends ControllerTestSupport {

	@Autowired
	private LogService logService;

	@DisplayName("활동일지 키워드들을 조회한다.")
	@Test
	void getAllLogKeywords() throws Exception {
		// given
		String message = "활동일지 키워드 조회 성공";

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
}
