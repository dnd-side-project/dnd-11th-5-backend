package com.odiga.fiesta.hello.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.odiga.fiesta.common.BasicResponse;

@RestController
public class HelloController {

	@GetMapping("/hello")
	public ResponseEntity<BasicResponse<String>> hello() {
		BasicResponse<String> response = BasicResponse.ok("API 응답 테스트", "Hello, world !");
		return ResponseEntity.ok(response);
	}

}
