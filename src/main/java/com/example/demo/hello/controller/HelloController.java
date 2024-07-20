package com.example.demo.hello.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.ApiResponse;

@RestController
public class HelloController {

	@GetMapping("/hello")
	public ResponseEntity<ApiResponse<String>> hello() {
		ApiResponse<String> response = ApiResponse.ok("API 응답 테스트", "Hello, world !");
		return ResponseEntity.ok(response);
	}

}
