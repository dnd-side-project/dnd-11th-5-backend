package com.odiga.fiesta.festival.controller;

import static com.odiga.fiesta.common.error.ErrorCode.*;
import static java.util.Objects.*;

import java.time.YearMonth;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.odiga.fiesta.common.BasicResponse;
import com.odiga.fiesta.common.PageResponse;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.festival.dto.request.FestivalFilterRequest;
import com.odiga.fiesta.festival.dto.response.FestivalBasic;
import com.odiga.fiesta.festival.dto.response.FestivalBookmarkResponse;
import com.odiga.fiesta.festival.dto.response.FestivalDetailResponse;
import com.odiga.fiesta.festival.dto.response.FestivalInfo;
import com.odiga.fiesta.festival.dto.response.FestivalMonthlyResponse;
import com.odiga.fiesta.festival.dto.response.FestivalThisWeekResponse;
import com.odiga.fiesta.festival.service.FestivalBookmarkService;
import com.odiga.fiesta.festival.service.FestivalService;
import com.odiga.fiesta.user.domain.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
	public ResponseEntity<BasicResponse<PageResponse<FestivalInfo>>> getFestivalsByDay(
		@AuthenticationPrincipal User user,
		@RequestParam(name = "year") @NotNull int year,
		@RequestParam(name = "month") @Min(1) @Max(12) @NotNull int month,
		@RequestParam(name = "day") int day,
		@PageableDefault(size = 6) Pageable pageable) {
		validateFestivalDay(year, month, day);

		String message = "페스티벌 일간 조회 성공";

		Page<FestivalInfo> festivalsByDay = festivalService.getFestivalsByDay(
			isNull(user) ? null : user.getId(), year, month, day, pageable);
		return ResponseEntity.ok(BasicResponse.ok(message, PageResponse.of(festivalsByDay)));
	}

	@Operation(
		summary = "필터를 사용한 페스티벌 조회",
		description = "필터와 정렬 조건을 사용하여 페스티벌을 다건 조회합니다."
	)
	@GetMapping("/filter")
	public ResponseEntity<BasicResponse<PageResponse<FestivalInfo>>> getFestivalsByFilters(
		@AuthenticationPrincipal User user,
		@ModelAttribute FestivalFilterRequest festivalFilterRequest,
		@RequestParam(value = "lat", required = false) Double latitude,
		@RequestParam(value = "lng", required = false) Double longitude,
		@PageableDefault(sort = {"startDate"}, direction = Sort.Direction.ASC, size = 6) Pageable pageable) {

		validateLatAndLng(latitude, longitude, pageable);

		Page<FestivalInfo> festivals = festivalService.getFestivalByFiltersAndSort(
			isNull(user) ? null : user.getId(), festivalFilterRequest, latitude, longitude, pageable);

		return ResponseEntity.ok(BasicResponse.ok("페스티벌 필터 조회 성공", PageResponse.of(festivals)));
	}

	@Operation(
		summary = "페스티벌 이름 검색",
		description = "페스티벌을 이름으로 검색합니다."
	)
	@GetMapping("/search")
	public ResponseEntity<BasicResponse<PageResponse<FestivalInfo>>> getFestivalsByQuery(
		@AuthenticationPrincipal User user,
		String query,
		@PageableDefault(size = 6) Pageable pageable) {

		validateQuery(query);

		Page<FestivalInfo> festivals = festivalService.getFestivalsByQuery(
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
		@AuthenticationPrincipal User user, @PathVariable Long festivalId) {

		FestivalDetailResponse festival = festivalService.getFestival(isNull(user) ? null : user.getId(), festivalId);
		return ResponseEntity.ok(BasicResponse.ok("페스티벌 상세 조회 성공", festival));
	}

	private void validateQuery(String query) {
		if (isNull(query) || query.isEmpty()) {
			throw new CustomException(QUERY_CANNOT_BE_EMPTY);
		}

		if (query.isBlank()) {
			throw new CustomException(QUERY_CANNOT_BE_BLANK);
		}
	}

	@Operation(
		summary = "이번 주 페스티벌 조회",
		description = "이번 주에 진행되고 있는 페스티벌을 조회한다."
	)
	@GetMapping("/thisweek")
	public ResponseEntity<BasicResponse<PageResponse<FestivalThisWeekResponse>>> getFestivalsInThisWeek(
		@PageableDefault(size = 3) Pageable pageable) {

		Page<FestivalThisWeekResponse> festivalsInThisWeek = festivalService.getFestivalsInThisWeek(pageable);
		return ResponseEntity.ok(BasicResponse.ok("이번 주 페스티벌 조회 성공", PageResponse.of(festivalsInThisWeek)));
	}

	@Operation(summary = "페스티벌 북마크 등록/해제", description = "페스티벌 북마크를 등록 또는 해제합니다.")
	@PatchMapping("/{festivalId}/bookmark")
	public ResponseEntity<BasicResponse<FestivalBookmarkResponse>> updateFestivalBookmark(
		@AuthenticationPrincipal User user,
		@PathVariable Long festivalId
	) {
		FestivalBookmarkResponse bookmark = festivalBookmarkService.updateFestivalBookmark(user, festivalId);
		return ResponseEntity.ok(BasicResponse.ok("페스티벌 북마크 등록/해제 성공", bookmark));
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

			if ("dist" .equals(property) && (isNull(latitude) || isNull(longitude))) {
				throw new CustomException(INVALID_CURRENT_LOCATION);
			}
		}
	}
}
