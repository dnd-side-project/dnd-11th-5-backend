package com.odiga.fiesta.festival.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.odiga.fiesta.common.BasicResponse;
import com.odiga.fiesta.festival.dto.response.FestivalMonthlyResponse;
import com.odiga.fiesta.festival.service.FestivalService;

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
}
