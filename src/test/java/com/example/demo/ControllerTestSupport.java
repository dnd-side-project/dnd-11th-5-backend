package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.hello.controller.HelloController;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = {
	HelloController.class, // 사용하는 컨트롤러 여기에 추가
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
