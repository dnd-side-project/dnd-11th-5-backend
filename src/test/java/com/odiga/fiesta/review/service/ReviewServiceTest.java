package com.odiga.fiesta.review.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.odiga.fiesta.MockTestSupport;
import com.odiga.fiesta.festival.repository.FestivalRepository;
import com.odiga.fiesta.review.dto.projection.ReviewDataWithLike;
import com.odiga.fiesta.review.dto.response.ReviewImageResponse;
import com.odiga.fiesta.review.dto.response.ReviewKeywordResponse;
import com.odiga.fiesta.review.dto.response.ReviewResponse;
import com.odiga.fiesta.review.dto.response.ReviewUserInfo;
import com.odiga.fiesta.review.repository.ReviewRepository;

class ReviewServiceTest extends MockTestSupport {

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private FestivalRepository festivalRepository;

	@InjectMocks
	private ReviewService reviewService;

	@DisplayName("리뷰 다건 조회 - 별점은 일의 자릿수, 소숫점 단위로 표시한다.")
	@Test
	void getReviews_RatingShouldDouble() {
		// given
		Long userId = 1L;
		Long festivalId = 1L;
		Pageable pageable = PageRequest.of(0, 10);

		given(festivalRepository.existsById(festivalId)).willReturn(true);
		
		ReviewDataWithLike reviewData =
			ReviewDataWithLike.builder()
				.reviewId(1L)
				.festivalId(festivalId)
				.user(new ReviewUserInfo(1L, "profileImage", "nickname"))
				.content("content")
				.createdAt(LocalDateTime.of(2021, 1, 1, 0, 0))
				.updatedAt(LocalDateTime.of(2021, 1, 1, 0, 0))
				.rating(50)
				.build();

		Page<ReviewDataWithLike> reviewsPage = new PageImpl<>(Collections.singletonList(reviewData), pageable, 1);
		given(reviewRepository.findReviews(userId, festivalId, pageable)).willReturn(reviewsPage);

		Map<Long, List<ReviewImageResponse>> reviewImagesMap = new HashMap<>();
		reviewImagesMap.put(1L, Arrays.asList(new ReviewImageResponse(1L, "imageUrl")));
		given(reviewRepository.findReviewImagesMap(any())).willReturn(reviewImagesMap);

		Map<Long, List<ReviewKeywordResponse>> reviewKeywordsMap = new HashMap<>();
		reviewKeywordsMap.put(1L, Arrays.asList(new ReviewKeywordResponse(1L, "keyword")));
		given(reviewRepository.findReviewKeywordsMap(any())).willReturn(reviewKeywordsMap);

		// when
		Page<ReviewResponse> reviews = reviewService.getReviews(userId, festivalId, pageable);

		// then
		assertEquals(1, reviews.getTotalElements());
		ReviewResponse review = reviews.getContent().get(0);
		assertEquals(5.0, review.getRating());
	}
}
