package com.odiga.fiesta.hello.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.odiga.fiesta.ControllerTestSupport;

class HelloControllerTest extends ControllerTestSupport {

	@DisplayName("/hello 요청을 보내면 요청이 성공한다.")
	@Test
	void requestHello() throws Exception {
		// given

		// when // then
		mockMvc.perform(
				get("/hello")
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.statusCode").value(200))
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("API 응답 테스트"))
			.andExpect(jsonPath("$.data").isString());
	}

	@DisplayName("정의되지 않은 url에 요청을 보내면 404 에러가 발생한다.")
	@Test
	void request404() throws Exception {
		// given

		// when // then
		mockMvc.perform(
				get("/hello123")
			)
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.statusCode").value(404));
	}
}
