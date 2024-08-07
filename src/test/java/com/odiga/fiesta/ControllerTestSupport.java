package com.odiga.fiesta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@WithMockUser
@WebMvcTest(controllers = {
	// 사용하는 컨트롤러 여기에 추가
})
public abstract class ControllerTestSupport {
	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	// 모킹할 빈 추가
	// @MockBean
	// protected ProductService productService;
}
