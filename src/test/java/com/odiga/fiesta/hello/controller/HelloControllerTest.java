package com.odiga.fiesta.hello.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.odiga.fiesta.ControllerTestSupport;

class HelloControllerTest extends ControllerTestSupport {

	@DisplayName("Hello 요청을 보낸다.")
	@Test
	void test() throws Exception {
		// given

		// when // then
		mockMvc.perform(
				get("/hello")
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("API 응답 테스트"))
			.andExpect(jsonPath("$.data").isString());
	}
}
