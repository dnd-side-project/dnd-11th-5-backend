package com.odiga.fiesta.festival.controller;

import static com.odiga.fiesta.common.error.ErrorCode.*;
import static java.util.Objects.*;

import java.time.YearMonth;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.odiga.fiesta.common.BasicResponse;
import com.odiga.fiesta.common.PageResponse;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.festival.dto.response.FestivalMonthlyResponse;
import com.odiga.fiesta.festival.service.FestivalService;
import com.odiga.fiesta.user.domain.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Tag(name = "Festival", description = "페스티벌 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/festivals")
public class FestivalController {

	private final FestivalService festivalService;

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

	// 페스티벌 일간 조회
	@Operation(
		summary = "페스티벌 일간 조회",
		description = "해당 날짜의 페스티벌을 조회합니다. 로그인 했을 때, 북마크 여부가 활성화 됩니다."
	)
	@GetMapping("/daily")
	public ResponseEntity<BasicResponse<PageResponse<FestivalMonthlyResponse>>> getFestivalsByDay(
		@AuthenticationPrincipal User user,
		@RequestParam(name = "year") @NotNull int year,
		@RequestParam(name = "month") @Min(1) @Max(12) @NotNull int month,
		@RequestParam(name = "day") int day,
		@PageableDefault(page = 0, size = 6) Pageable pageable
	) {
		validateFestivalDay(year, month, day);

		String message = "페스티벌 일간 조회 성공";

		final BasicResponse response = BasicResponse.builder()
			.message(message)
			.status(HttpStatus.OK)
			.data(new PageResponse(festivalService.getFestivalsByDay(isNull(user) ? null : user.getId(), year, month, day, pageable)))
			.build();

		return ResponseEntity.ok(response);
	}

	private void validateFestivalDay(int year, int month, int day) {
		YearMonth yearMonth = YearMonth.of(year, month);

		if (!yearMonth.isValidDay(day)) {
			throw new CustomException(INVALID_FESTIVAL_DATE);
		}
	}

	// 페스티벌 다건 조회

}
