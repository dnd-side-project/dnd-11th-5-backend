package com.odiga.fiesta.festival.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.odiga.fiesta.common.BasicResponse;
import com.odiga.fiesta.festival.dto.response.CategoryResponse;
import com.odiga.fiesta.festival.dto.response.CompanionResponse;
import com.odiga.fiesta.festival.dto.response.MoodResponse;
import com.odiga.fiesta.festival.dto.response.PriorityResponse;
import com.odiga.fiesta.festival.service.CategoryService;
import com.odiga.fiesta.festival.service.CompanionService;
import com.odiga.fiesta.festival.service.MoodService;
import com.odiga.fiesta.festival.service.PriorityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Festival", description = "페스티벌 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/festivals")
public class FestivalStaticDataController {

	private final MoodService moodService;
	private final CategoryService categoryService;
	private final CompanionService companionService;
	private final PriorityService priorityService;

	@Operation(
		summary = "모든 페스티벌 분위기 타입 조회",
		description = "페스티벌 분위기 타입들을 조회합니다."
	)
	@GetMapping("/moods")
	public ResponseEntity<BasicResponse<List<MoodResponse>>> getAllMoods() {
		final BasicResponse<List<MoodResponse>> moodResponses = BasicResponse.ok(
			"페스티벌 분위기 조회 성공", moodService.getAllMoods());
		return ResponseEntity.ok(moodResponses);
	}

	@Operation(
		summary = "모든 페스티벌 분류 조회",
		description = "페스티벌 분류들을 조회합니다."
	)
	@GetMapping("/categories")
	public ResponseEntity<BasicResponse<List<CategoryResponse>>> getCategories() {
		final BasicResponse<List<CategoryResponse>> categoryResponses = BasicResponse.ok(
			"페스티벌 카테고리 조회 성공", categoryService.getAllCategories());
		return ResponseEntity.ok(categoryResponses);
	}

	@Operation(
		summary = "모든 일행 분류 조회",
		description = "일행 분류들을 조회합니다."
	)
	@GetMapping("/companions")
	public ResponseEntity<BasicResponse<List<CompanionResponse>>> getCompanions() {
		final BasicResponse<List<CompanionResponse>> companionResponses = BasicResponse.ok(
			"페스티벌 일행 분류 조회 성공", companionService.getAllCompanions());
		return ResponseEntity.ok(companionResponses);
	}

	@Operation(
		summary = "모든 페스티벌 우선순위 분류 조회",
		description = "페스티벌 우선순위 분류들을 조회합니다."
	)
	@GetMapping("/priorities")
	public ResponseEntity<BasicResponse<List<PriorityResponse>>> getPriorities() {
		final BasicResponse<List<PriorityResponse>> priorityResponses = BasicResponse.ok(
			"페스티벌 우선순위 조회 성공", priorityService.getAllPriorities());
		return ResponseEntity.ok(priorityResponses);
	}
}
