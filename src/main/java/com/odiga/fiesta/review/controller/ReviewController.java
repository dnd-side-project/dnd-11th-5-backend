package com.odiga.fiesta.review.controller;

import static java.util.Objects.*;

import com.odiga.fiesta.auth.domain.AuthUser;
import com.odiga.fiesta.common.BasicResponse;
import com.odiga.fiesta.common.PageResponse;
import com.odiga.fiesta.review.dto.response.ReviewKeywordResponse;
import com.odiga.fiesta.review.dto.response.ReviewResponse;
import com.odiga.fiesta.review.dto.response.TopReviewResponse;
import com.odiga.fiesta.review.service.ReviewService;
import com.odiga.fiesta.user.domain.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Review", description = "Review 관련 API")
public class ReviewController {

	private final ReviewService reviewService;

	@GetMapping
	@Operation(summary = "리뷰 다건 조회", description = "페스티벌에 대한 리뷰를 다건 조회합니다.")
	public ResponseEntity<BasicResponse<PageResponse<ReviewResponse>>> getReviews(
		@AuthUser User user,
		@RequestParam Long festivalId,
		@PageableDefault(size = 6, sort = "date", direction = Sort.Direction.DESC) Pageable pageable
	) {

		Page<ReviewResponse> reviews = reviewService.getReviews(isNull(user) ? null : user.getId(), festivalId,
			pageable);
		return ResponseEntity.ok(BasicResponse.ok("리뷰 다건 조회 성공", PageResponse.of(reviews)));
	}

	@GetMapping("/mostlike")
	@Operation(summary = "리뷰 TOP3 조회", description = "리뷰 TOP3를 조회합니다.")
	public ResponseEntity<BasicResponse<List<TopReviewResponse>>> getReviews() {

		List<TopReviewResponse> reviews = reviewService.getTop3Reviews();

		String message = "리뷰 TOP3 조회 성공";

		return ResponseEntity.ok(BasicResponse.ok(message, reviews));
	}

	@GetMapping("/keywords/top")
	@Operation(summary = "가장 많이 선택된 키워드 TOP5 조회", description = "상위 5개 키워드를 조회합니다. 선택 갯수가 동률일 경우, 최근에 선택된 키워드를 조회합니다.")
	public ResponseEntity<BasicResponse<List<ReviewKeywordResponse>>> getTop5Keywords(@RequestParam Long festivalId) {

		List<ReviewKeywordResponse> keywords = reviewService.getTop5Keywords(festivalId);

		String message = "가장 많이 선택된 키워드 TOP5 조회 성공";

		return ResponseEntity.ok(BasicResponse.ok(message, keywords));
	}
}
