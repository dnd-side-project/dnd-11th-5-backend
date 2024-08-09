package com.odiga.fiesta.log.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.odiga.fiesta.common.BasicResponse;
import com.odiga.fiesta.log.dto.response.LogDetailResponse;
import com.odiga.fiesta.log.dto.response.LogKeywordResponse;
import com.odiga.fiesta.log.service.LogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

	// TODO: 권한 관련 구현, 권한 관련 테스트
	@Operation(
		summary = "활동일지 상세 조회",
		description = "활동일지를 상세 조회합니다. 활동일지는 자기 자신의 활동일지만 조회할 수 있습니다."
	)
	@GetMapping("/{logId}")
	public ResponseEntity<BasicResponse<LogDetailResponse>> getLogDetail(
		@Parameter(name = "logId", description = "조회하고자하는 활동일지의 ID, path variable")
		// TODO: 시큐리티, User 관련 설정 셋팅 후 활성화
		// @AuthenticationPrincipal CustomUserDetail user,
		@PathVariable Long logId) {

		final BasicResponse<LogDetailResponse> logKeywordResponses = BasicResponse.ok(
		"방문일지 상세 조회 성공", logService.getLogDetail(logId));

		return ResponseEntity.ok(logKeywordResponses);
	}

}
