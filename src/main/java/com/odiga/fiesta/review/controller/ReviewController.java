package com.odiga.fiesta.review.controller;

import com.odiga.fiesta.common.BasicResponse;
import com.odiga.fiesta.review.dto.ReviewResponse;
import com.odiga.fiesta.review.service.ReviewService;
import com.odiga.fiesta.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Review", description = "Review 관련 API")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/mostlike")
    @Operation(summary = "리뷰 TOP3 조회", description = "실시간 핫한 후기를 조회합니다.")
    public ResponseEntity<BasicResponse<ReviewResponse.getTop3ReviewsDTO>> getReviews() {

        ReviewResponse.getTop3ReviewsDTO reviews = reviewService.getTop3ReviewsDTO();

        String message = "리뷰 TOP3 조회 성공";

        return ResponseEntity.ok(BasicResponse.ok(message, reviews));
    }

    @GetMapping
    @Operation(summary = "리뷰 다건 조회", description = "페스티벌에 대한 리뷰를 다건 조회합니다.")
    public ResponseEntity<BasicResponse<ReviewResponse.getReviewsDTO>> getReviews(Principal principal,
                                                                                        @RequestParam(required = false) Long festivalId,
                                                                                        @RequestParam(required = false, defaultValue = "date") String sort,
                                                                                        @RequestParam(required = false, defaultValue = "0") int page,
                                                                                        @RequestParam(required = false, defaultValue = "6") int size) {

        Long userId = Long.parseLong(principal.getName());

        ReviewResponse.getReviewsDTO reviews = reviewService.getReviews(userId, festivalId, sort, page, size);

        String message = "리뷰 다건 조회 성공";

        return ResponseEntity.ok(BasicResponse.ok(message, reviews));
    }
}
