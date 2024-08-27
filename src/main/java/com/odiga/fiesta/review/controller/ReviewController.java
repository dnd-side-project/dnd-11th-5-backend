package com.odiga.fiesta.review.controller;

import static java.util.Objects.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.odiga.fiesta.auth.domain.AuthUser;
import com.odiga.fiesta.common.BasicResponse;
import com.odiga.fiesta.common.PageResponse;
import com.odiga.fiesta.review.dto.response.ReviewKeywordResponse;
import com.odiga.fiesta.review.dto.response.ReviewResponse;
import com.odiga.fiesta.review.dto.response.ReviewSimpleResponse;
import com.odiga.fiesta.review.dto.response.TopReviewKeywordsResponse;
import com.odiga.fiesta.review.service.ReviewService;
import com.odiga.fiesta.user.domain.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Review", description = "Review 관련 API")
public class ReviewController {

	private final ReviewService reviewService;

	@GetMapping("/keywords")
	@Operation(summary = "모든 리뷰 키워드 조회", description = "리뷰 키워드 목록을 조회합니다.")
	public ResponseEntity<BasicResponse<List<ReviewKeywordResponse>>> getKeywords() {

		List<ReviewKeywordResponse> keywords = reviewService.getKeywords();
		return ResponseEntity.ok(BasicResponse.ok("리뷰 키워드 조회 성공", keywords));
	}

	@GetMapping
	@Operation(summary = "리뷰 다건 조회", description = "페스티벌에 대한 리뷰를 다건 조회합니다.")
	public ResponseEntity<BasicResponse<PageResponse<ReviewResponse>>> getReviews(
		@AuthUser User user,
		@RequestParam Long festivalId,
		@PageableDefault(size = 6, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {

		Page<ReviewResponse> reviews = reviewService.getReviews(isNull(user) ? null : user.getId(), festivalId,
			pageable);
		return ResponseEntity.ok(BasicResponse.ok("리뷰 다건 조회 성공", PageResponse.of(reviews)));
	}

	@GetMapping("/mostlike")
	@Operation(summary = "실시간 가장 핫한 리뷰 조회", description = "서비스에서 가장 좋아요를 많이 받은 리뷰 순으로 조회합니다.")
	public ResponseEntity<BasicResponse<List<ReviewSimpleResponse>>> getReviews(
		@RequestParam(required = false, defaultValue = "3") Long size
	) {
		List<ReviewSimpleResponse> mostLikeReviews = reviewService.getMostLikeReviews(size);
		return ResponseEntity.ok(BasicResponse.ok("실시간 가장 핫한 리뷰 조회 성공", mostLikeReviews));
	}

	@GetMapping("/keywords/top")
	@Operation(summary = "페스티벌에서 가장 많이 선택된 리뷰 키워드 조회", description = "페스티벌 리뷰들의 상위 5개 키워드를 조회합니다. 선택 갯수가 동률일 경우, 최근에 선택된 키워드를 조회합니다.")
	public ResponseEntity<BasicResponse<TopReviewKeywordsResponse>> getTopReviewKeywords(
		@RequestParam Long festivalId,
		@RequestParam(required = false, defaultValue = "5") Long size) {
		TopReviewKeywordsResponse keywords = reviewService.getTopReviewKeywords(festivalId, size);
		return ResponseEntity.ok(BasicResponse.ok("가장 많이 선택된 키워드 조회 성공", keywords));
	}
}
