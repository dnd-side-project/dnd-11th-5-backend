package com.odiga.fiesta.log.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.odiga.fiesta.common.BasicResponse;
import com.odiga.fiesta.log.dto.response.LogKeywordResponse;
import com.odiga.fiesta.log.service.LogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Log", description = "활동일지 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/logs")
public class LogController {

	private final LogService logService;

	@Operation(
		summary = "활동일지 키워드 조회",
		description = "활동일지 키워드들을 조회합니다."
	)
	@GetMapping("/keywords")
	public ResponseEntity<BasicResponse<List<LogKeywordResponse>>> getAllMoods() {
		final BasicResponse<List<LogKeywordResponse>> logKeywordResponses = BasicResponse.ok(
			"활동일지 키워드 조회 성공", logService.getAllLogKeywords());
		return ResponseEntity.ok(logKeywordResponses);
	}
}
