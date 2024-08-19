package com.odiga.fiesta.log.controller;

import static org.springframework.http.MediaType.*;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.odiga.fiesta.common.BasicResponse;
import com.odiga.fiesta.common.error.ErrorCode;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.common.util.FileUtils;
import com.odiga.fiesta.log.dto.request.LogCreateRequest;
import com.odiga.fiesta.log.dto.response.LogDetailResponse;
import com.odiga.fiesta.log.dto.response.LogIdResponse;
import com.odiga.fiesta.log.dto.response.LogKeywordResponse;
import com.odiga.fiesta.log.service.LogService;
import com.odiga.fiesta.user.domain.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Log", description = "방문일지 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/logs")
@Slf4j
public class LogController {

	private final LogService logService;
	private final FileUtils fileUtils;

	@Operation(
		summary = "방문일지 키워드 조회",
		description = "방문일지 키워드들을 조회합니다."
	)
	@GetMapping("/keywords")
	public ResponseEntity<BasicResponse<List<LogKeywordResponse>>> getAllMoods() {
		final BasicResponse<List<LogKeywordResponse>> logKeywordResponses = BasicResponse.ok(
			"방문일지 키워드 조회 성공", logService.getAllLogKeywords());
		return ResponseEntity.ok(logKeywordResponses);
	}

	@Operation(
		summary = "방문일지 생성",
		description = "방문일지를 생성합니다."
	)
	@PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<BasicResponse<LogIdResponse>> createLog(@AuthenticationPrincipal User user,
		@RequestPart(value = "data") @Valid LogCreateRequest request,
		@RequestPart(required = false) List<MultipartFile> files
	) {
		validateFileCount(files);
		validFilesExtension(files);

		final LogIdResponse response = logService.createLog(user.getId(), request, files);

		return ResponseEntity.created(URI.create("/api/v1/logs/" + response.getLogId()))
			.body(BasicResponse.created("방문일지 생성 완료", response));
	}

	// TODO: 권한 관련 구현, 권한 관련 테스트
	@Operation(
		summary = "방문일지 상세 조회",
		description = "방문일지를 상세 조회합니다. 방문일지는 자기 자신의 방문일지만 조회할 수 있습니다."
	)
	@GetMapping("/{logId}")
	public ResponseEntity<BasicResponse<LogDetailResponse>> getLogDetail(
		@Parameter(name = "logId", description = "조회하고자하는 방문일지의 ID, path variable")
		// TODO: 시큐리티, User 관련 설정 셋팅 후 활성화
		// @AuthenticationPrincipal CustomUserDetail user,
		@PathVariable Long logId) {

		final BasicResponse<LogDetailResponse> logKeywordResponses = BasicResponse.ok(
			"방문일지 상세 조회 성공", logService.getLogDetail(logId));

		return ResponseEntity.ok(logKeywordResponses);
	}

	private void validateFileCount(List<MultipartFile> files) {
		if (files.size() > 3) {
			throw new CustomException(ErrorCode.LOG_IMAGE_COUNT_EXCEEDED);
		}
	}

	private void validFilesExtension(List<MultipartFile> files) {
		files.stream()
			.map(file -> fileUtils.getFileExtension(file.getOriginalFilename()))
			.forEach(fileUtils::validateImageExtension);
	}
}
