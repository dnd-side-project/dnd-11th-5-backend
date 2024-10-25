package com.odiga.fiesta.festival.controller;

import static com.odiga.fiesta.common.error.ErrorCode.*;
import static java.util.Objects.*;
import static org.springframework.http.MediaType.*;

import java.net.URI;
import java.time.YearMonth;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.odiga.fiesta.auth.domain.AuthUser;
import com.odiga.fiesta.common.BasicResponse;
import com.odiga.fiesta.common.PageResponse;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.festival.dto.request.CreateFestivalModificationRequest;
import com.odiga.fiesta.festival.dto.request.FestivalCreateRequest;
import com.odiga.fiesta.festival.dto.request.FestivalFilterRequest;
import com.odiga.fiesta.festival.dto.response.FestivalAndLocation;
import com.odiga.fiesta.festival.dto.response.FestivalBasic;
import com.odiga.fiesta.festival.dto.response.FestivalBookmarkResponse;
import com.odiga.fiesta.festival.dto.response.FestivalCreateResponse;
import com.odiga.fiesta.festival.dto.response.FestivalDetailResponse;
import com.odiga.fiesta.festival.dto.response.FestivalInfo;
import com.odiga.fiesta.festival.dto.response.FestivalInfoWithBookmark;
import com.odiga.fiesta.festival.dto.response.FestivalModificationResponse;
import com.odiga.fiesta.festival.dto.response.FestivalMonthlyResponse;
import com.odiga.fiesta.festival.dto.response.RecommendFestivalResponse;
import com.odiga.fiesta.festival.service.FestivalBookmarkService;
import com.odiga.fiesta.festival.service.FestivalService;
import com.odiga.fiesta.user.domain.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Festival", description = "페스티벌 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/festivals")
@Slf4j
public class FestivalController {

	private static final String KEYWORD_RANKING_KEY = "festival rank";

	private final FestivalService festivalService;
	private final FestivalBookmarkService festivalBookmarkService;

	@Operation(
		summary = "모든 페스티벌의 id 조회",
		description = "DB에 존재하는 모든 페스티벌의 id를 조회합니다."
	)
	@GetMapping("/id")
	public ResponseEntity<BasicResponse<List<Long>>> getAllFestivalIds() {
		String message = "페스티벌 id 조회 성공";
		final List<Long> response = festivalService.getAllFestivalIds();
		return ResponseEntity.ok(BasicResponse.ok(message, response));
	}

	@Operation(
		summary = "페스티벌 생성",
		description = "페스티벌을 생성합니다."
	)
	@PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<BasicResponse<FestivalCreateResponse>> createFestival(
		@AuthUser User user,
		@Parameter(name = "data", schema = @Schema(type = "string", format = "binary"), description = "등록할 페스티벌 데이터")
		@RequestPart(value = "data") @Valid FestivalCreateRequest request,
		@RequestPart(value = "images", required = false) List<MultipartFile> images
	) {

		checkLogin(user);

		FestivalBasic festival = festivalService.createFestival(user.getId(), request, images);
		return ResponseEntity.created(
				URI.create("/api/v1/festivals/" + festival.getFestivalId()))
			.body(BasicResponse.created("페스티벌 생성 성공", FestivalCreateResponse.of(festival)));
	}

	@Operation(
		summary = "페스티벌 월간 조회",
		description = "페스티벌을 월간 조회합니다. 해당 월 기준으로 일자별 페스티벌이 리턴됩니다."
	)
	@GetMapping("/monthly")
	public ResponseEntity<BasicResponse<FestivalMonthlyResponse>> getMonthlyFestivals(
		@RequestParam(name = "year") @NotNull int year,
		@RequestParam(name = "month") @Min(1) @Max(12) @NotNull int month
	) {
		String message = "페스티벌 월간 조회 성공";
		final FestivalMonthlyResponse response = festivalService.getMonthlyFestivals(year, month);
		return ResponseEntity.ok(BasicResponse.ok(message, response));
	}

	@Operation(
		summary = "페스티벌 일간 조회",
		description = "해당 날짜의 페스티벌을 조회합니다."
	)
	@GetMapping("/daily")
	public ResponseEntity<BasicResponse<PageResponse<FestivalInfoWithBookmark>>> getFestivalsByDay(
		@AuthUser User user,
		@RequestParam(name = "year") @NotNull int year,
		@RequestParam(name = "month") @Min(1) @Max(12) @NotNull int month,
		@RequestParam(name = "day") int day,
		@ParameterObject @Parameter(description = "Paging parameters", example = "{\"page\":0,\"size\":6}")
		@PageableDefault(size = 6) Pageable pageable) {
		validateFestivalDay(year, month, day);

		String message = "페스티벌 일간 조회 성공";

		Page<FestivalInfoWithBookmark> festivalsByDay = festivalService.getFestivalsByDay(
			isNull(user) ? null : user.getId(), year, month, day, pageable);
		return ResponseEntity.ok(BasicResponse.ok(message, PageResponse.of(festivalsByDay)));
	}

	@Operation(
		summary = "필터를 사용한 페스티벌 조회",
		description = "필터와 정렬 조건을 사용하여 페스티벌을 다건 조회합니다."
	)
	@GetMapping("/filter")
	public ResponseEntity<BasicResponse<PageResponse<FestivalInfoWithBookmark>>> getFestivalsByFilters(
		@AuthUser User user,
		@ModelAttribute FestivalFilterRequest festivalFilterRequest,
		@RequestParam(value = "lat", required = false) Double latitude,
		@RequestParam(value = "lng", required = false) Double longitude,
		@ParameterObject @Parameter(description = "Paging parameters", example = "{\"page\":0,\"size\":6,\"sort\":[\"startDate,asc\"]}")
		@PageableDefault(sort = {"startDate"}, direction = Sort.Direction.ASC, size = 6) Pageable pageable) {

		validateLatAndLng(latitude, longitude, pageable);

		Page<FestivalInfoWithBookmark> festivals = festivalService.getFestivalByFiltersAndSort(
			isNull(user) ? null : user.getId(), festivalFilterRequest, latitude, longitude, pageable);

		return ResponseEntity.ok(BasicResponse.ok("페스티벌 필터 조회 성공", PageResponse.of(festivals)));
	}

	@Operation(
		summary = "페스티벌 이름 검색",
		description = "페스티벌을 이름으로 검색합니다."
	)
	@GetMapping("/search")
	public ResponseEntity<BasicResponse<PageResponse<FestivalInfoWithBookmark>>> getFestivalsByQuery(
		@AuthUser User user,
		String query,
		@ParameterObject @Parameter(description = "Paging parameters", example = "{\"page\":0,\"size\":6}")
		@PageableDefault(size = 6) Pageable pageable) {

		validateQuery(query);

		Page<FestivalInfoWithBookmark> festivals = festivalService.getFestivalsByQuery(
			isNull(user) ? null : user.getId(), query, pageable);

		List<CompletableFuture<Void>> futures = festivals.getContent().stream()
			.map(festival -> CompletableFuture.runAsync(() ->
				festivalService.updateSearchRanking(
					KEYWORD_RANKING_KEY,
					FestivalBasic.of(festival.getFestivalId(), festival.getName())
				)))
			.toList();

		return ResponseEntity.ok(BasicResponse.ok("페스티벌 이름 검색 성공", PageResponse.of(festivals)));
	}

	@Operation(
		summary = "다가오는 페스티벌 조회",
		description = "유저가 북마크 한 페스티벌 중 시작일이 빠른 순으로 조회합니다."
	)
	@GetMapping("/upcoming")
	public ResponseEntity<BasicResponse<PageResponse<FestivalAndLocation>>> getUpcomingFestival(
		@AuthUser User user,
		@ParameterObject @Parameter(description = "Paging parameters", example = "{\"page\":0,\"size\":3}")
		@PageableDefault(size = 3) Pageable pageable) {

		checkLogin(user);

		Page<FestivalAndLocation> festivals = festivalService.getUpcomingFestival(user.getId(), pageable);
		return ResponseEntity.ok(BasicResponse.ok("다가오는 페스티벌 조회 성공", PageResponse.of(festivals)));
	}

	@Operation(
		summary = "실시간 급상승 페스티벌 조회",
		description = "실시간 급상승 페스티벌을 조회합니다."
	)
	@GetMapping("/trending")
	public ResponseEntity<BasicResponse<PageResponse<FestivalBasic>>> getFestivalsByQuery(
		@RequestParam(required = false, defaultValue = "0") Long page,
		@RequestParam(required = false, defaultValue = "5") Integer size) {

		PageResponse<FestivalBasic> trendingFestival = festivalService.getTrendingFestival(KEYWORD_RANKING_KEY, page,
			size);

		return ResponseEntity.ok(BasicResponse.ok("실시간 급상승 페스티벌 조회 성공", trendingFestival));
	}

	@Operation(
		summary = "페스티벌 상세 조회",
		description = "페스티벌을 상세 조회합니다."
	)
	@GetMapping("/{festivalId}")
	public ResponseEntity<BasicResponse<FestivalDetailResponse>> getFestival(
		@AuthUser User user, @PathVariable Long festivalId) {

		FestivalDetailResponse festival = festivalService.getFestival(isNull(user) ? null : user.getId(), festivalId);
		return ResponseEntity.ok(BasicResponse.ok("페스티벌 상세 조회 성공", festival));
	}

	@Operation(
		summary = "이번 주 페스티벌 조회",
		description = "이번 주에 진행되고 있는 페스티벌을 조회한다."
	)
	@GetMapping("/thisweek")
	public ResponseEntity<BasicResponse<PageResponse<FestivalInfo>>> getFestivalsInThisWeek(
		@ParameterObject @Parameter(description = "Paging parameters", example = "{\"page\":0,\"size\":3}")
		@PageableDefault(size = 3) Pageable pageable) {

		Page<FestivalInfo> festivalsInThisWeek = festivalService.getFestivalsInThisWeek(pageable);
		return ResponseEntity.ok(BasicResponse.ok("이번 주 페스티벌 조회 성공", PageResponse.of(festivalsInThisWeek)));
	}

	@Operation(summary = "페스티벌 북마크 등록/해제", description = "페스티벌 북마크를 등록 또는 해제합니다.")
	@PatchMapping("/{festivalId}/bookmark")
	public ResponseEntity<BasicResponse<FestivalBookmarkResponse>> updateFestivalBookmark(
		@AuthUser User user,
		@PathVariable Long festivalId) {
		checkLogin(user);
		FestivalBookmarkResponse bookmark = festivalBookmarkService.updateFestivalBookmark(user, festivalId);
		return ResponseEntity.ok(BasicResponse.ok("페스티벌 북마크 등록/해제 성공", bookmark));
	}

	@Operation(summary = "HOT 페스티벌 조회", description = "스크랩 수 순서대로 페스티벌을 조회합니다.")
	@GetMapping("/mostlike")
	public ResponseEntity<BasicResponse<PageResponse<FestivalInfo>>> getHotFestival(
		@ParameterObject @Parameter(description = "Paging parameters", example = "{\"page\":0,\"size\":6}")
		@PageableDefault(size = 6) Pageable pageable
	) {

		Page<FestivalInfo> hotFestivals = festivalService.getHotFestivals(pageable);
		return ResponseEntity.ok(BasicResponse.ok("HOT 페스티벌 조회 성공", PageResponse.of(hotFestivals)));
	}

	@Operation(summary = "유형별 추천 페스티벌 조회", description = "유저 유형 별 추천 페스티벌을 랜덤으로 조회합니다.")
	@GetMapping("/recommend")
	public ResponseEntity<BasicResponse<RecommendFestivalResponse>> getRecommendFestival(
		@AuthUser User user,
		@RequestParam(required = false, defaultValue = "5") Long size
	) {
		checkLogin(user);

		RecommendFestivalResponse recommendFestivals = festivalService.getRecommendFestivals(
			isNull(user) ? null : user, size);

		return ResponseEntity.ok(BasicResponse.ok("유형별 추천 페스티벌 조회 성공", recommendFestivals));
	}

	@Operation(summary = "페스티벌 수정 사항 요청", description = "페스티벌 수정 사항 요청을 생성합니다.")
	@PostMapping("/{festivalId}/request")
	public ResponseEntity<BasicResponse<FestivalModificationResponse>> createFestivalRequest(
		@AuthUser User user,
		@PathVariable Long festivalId,
		@RequestBody @Valid CreateFestivalModificationRequest request
	) {
		checkLogin(user);

		FestivalModificationResponse response = festivalService.createFestivalRequest(
			isNull(user) ? null : user, festivalId, request);

		return ResponseEntity.created(
				URI.create("/api/v1/festivals/" + festivalId))
			.body(BasicResponse.created("페스티벌 수정 사항 요청 성공", response));
	}

	private void validateFestivalDay(int year, int month, int day) {
		YearMonth yearMonth = YearMonth.of(year, month);

		if (!yearMonth.isValidDay(day)) {
			throw new CustomException(INVALID_FESTIVAL_DATE);
		}
	}

	private static void validateLatAndLng(Double latitude, Double longitude, Pageable pageable) {
		for (Sort.Order order : pageable.getSort()) {
			String property = order.getProperty();

			if ("dist".equals(property) && (isNull(latitude) || isNull(longitude))) {
				throw new CustomException(INVALID_CURRENT_LOCATION);
			}
		}
	}

	private void validateQuery(String query) {
		if (isNull(query) || query.isEmpty()) {
			throw new CustomException(QUERY_CANNOT_BE_EMPTY);
		}

		if (query.isBlank()) {
			throw new CustomException(QUERY_CANNOT_BE_BLANK);
		}
	}

	private void checkLogin(User user) {
		if (isNull(user)) {
			throw new CustomException(NOT_LOGGED_IN);
		}
	}
}
