package com.odiga.fiesta.review.service;

import static com.odiga.fiesta.common.error.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.odiga.fiesta.IntegrationTestSupport;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.repository.FestivalRepository;
import com.odiga.fiesta.keyword.domain.Keyword;
import com.odiga.fiesta.keyword.repository.KeywordRepository;
import com.odiga.fiesta.review.domain.Review;
import com.odiga.fiesta.review.domain.ReviewKeyword;
import com.odiga.fiesta.review.domain.ReviewLike;
import com.odiga.fiesta.review.dto.request.ReviewReportRequest;
import com.odiga.fiesta.review.dto.response.ReviewReportResponse;
import com.odiga.fiesta.review.dto.response.ReviewSimpleResponse;
import com.odiga.fiesta.review.dto.response.TopReviewKeywordsResponse;
import com.odiga.fiesta.review.repository.ReviewKeywordRepository;
import com.odiga.fiesta.review.repository.ReviewLikeRepository;
import com.odiga.fiesta.review.repository.ReviewReportRepository;
import com.odiga.fiesta.review.repository.ReviewRepository;
import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.repository.UserRepository;

class ReviewServiceTest extends IntegrationTestSupport {

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private FestivalRepository festivalRepository;

	@Autowired
	private KeywordRepository keywordRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ReviewLikeRepository reviewLikeRepository;

	@Autowired
	private ReviewKeywordRepository reviewKeywordRepository;

	@Autowired
	private ReviewReportRepository reviewReportRepository;

	List<Keyword> keywords;
	Festival festival;

	User user;

	@BeforeEach
	void setUp() {
		// 키워드, 페스티벌, 현재 유저 셋업
		keywords = keywordRepository.saveAll(Arrays.asList(
			Keyword.builder().content("키워드1").build(),
			Keyword.builder().content("키워드2").build()
		));

		festival = festivalRepository.save(
			Festival.builder()
				.userId(1L)
				.name("페스티벌")
				.startDate(LocalDate.of(2021, 1, 1))
				.endDate(LocalDate.of(2021, 1, 2))
				.address("주소")
				.sidoId(1L)
				.sigungu("시군구")
				.latitude(1.0)
				.longitude(1.0)
				.tip("팁")
				.homepageUrl("홈페이지")
				.instagramUrl("인스타그램")
				.fee("요금")
				.description("설명")
				.playtime("행사 시간")
				.isPending(false)
				.build()
		);

		user = userRepository.save(User.builder()
			.id(1L)
			.email("fiesta@odiga.com")
			.userTypeId(1L)
			.nickname("피에스타")
			.statusMessage("상태 메시지")
			.profileImage("프로필 이미지")
			.build());
	}

	@AfterEach
	void tearDown() {
		reviewRepository.deleteAll();
		festivalRepository.deleteAll();
		keywordRepository.deleteAll();
		userRepository.deleteAll();
	}
	
	@DisplayName("실시간 가장 핫한 후기 조회 - 좋아요 순 정렬")
	@Test
	void getMostLikeReviews() {
		// given
		Review reviewLike5 = createReview();
		Review reviewLike3 = createReview();
		Review reviewLike2 = createReview();
		Review reviewLike1 = createReview();

		reviewRepository.saveAll(Arrays.asList(reviewLike5, reviewLike3, reviewLike2, reviewLike1));

		for (long i = 1; i <= 5; i++) {
			reviewLikeRepository.save(createReviewLike(i, reviewLike5.getId()));
		}

		for (long i = 1; i <= 3; i++) {
			reviewLikeRepository.save(createReviewLike(i, reviewLike3.getId()));
		}

		for (long i = 1; i <= 2; i++) {
			reviewLikeRepository.save(createReviewLike(i, reviewLike2.getId()));
		}

		for (long i = 1; i <= 1; i++) {
			reviewLikeRepository.save(createReviewLike(i, reviewLike1.getId()));
		}

		// when
		List<ReviewSimpleResponse> mostLikeReviews = reviewService.getMostLikeReviews(3L);

		// then
		assertThat(mostLikeReviews).hasSize(3)
			.extracting("reviewId", "festivalId", "content", "rating", "thumbnailImage")
			.containsExactly(
				tuple(reviewLike5.getId(), festival.getId(), "리뷰 내용", 3.5, null),
				tuple(reviewLike3.getId(), festival.getId(), "리뷰 내용", 3.5, null),
				tuple(reviewLike2.getId(), festival.getId(), "리뷰 내용", 3.5, null)
			);
	}

	@DisplayName("가장 많이 선택된 리뷰 키워드 조회")
	@Test
	void getTopReviewKeywords() {
		// given
		Review review = createReview();
		reviewRepository.save(review);

		ReviewKeyword reviewKeyword1 = createReviewKeyword(review.getId(), keywords.get(0).getId());
		ReviewKeyword reviewKeyword2 = createReviewKeyword(review.getId(), keywords.get(0).getId());
		ReviewKeyword reviewKeyword3 = createReviewKeyword(review.getId(), keywords.get(1).getId());
		reviewKeywordRepository.saveAll(Arrays.asList(reviewKeyword1, reviewKeyword2, reviewKeyword3));

		// when
		TopReviewKeywordsResponse topReviewKeywords = reviewService.getTopReviewKeywords(festival.getId(), 5L);

		// then
		assertEquals(3L, topReviewKeywords.getTotalCount());

		assertThat(topReviewKeywords.getKeywords()).hasSize(2)
			.extracting("keywordId", "keyword", "selectionCount")
			.containsExactly(
				tuple(keywords.get(0).getId(), keywords.get(0).getContent(), 2L),
				tuple(keywords.get(1).getId(), keywords.get(1).getContent(), 1L)
			);
	}

	@DisplayName("가장 많이 선택된 리뷰 키워드 조회 - 선택갯수가 동률일 경우")
	@Test
	void getTopReviewKeywords_SelectionCountEqual() {
		// given
		Review review = createReview();
		reviewRepository.save(review);

		ReviewKeyword reviewKeyword1 = createReviewKeyword(review.getId(), keywords.get(0).getId(),
			LocalDateTime.of(2021, 1, 1, 0, 0));
		ReviewKeyword reviewKeyword2 = createReviewKeyword(review.getId(), keywords.get(0).getId(),
			LocalDateTime.of(2021, 1, 1, 0, 0));
		ReviewKeyword reviewKeyword3 = createReviewKeyword(review.getId(), keywords.get(1).getId(),
			LocalDateTime.of(2021, 1, 1, 0, 0));
		ReviewKeyword reviewKeyword4 = createReviewKeyword(review.getId(), keywords.get(1).getId(),
			LocalDateTime.of(2021, 1, 2, 0, 0));
		reviewKeywordRepository.saveAll(Arrays.asList(reviewKeyword1, reviewKeyword2, reviewKeyword3, reviewKeyword4));

		// when
		TopReviewKeywordsResponse topReviewKeywords = reviewService.getTopReviewKeywords(festival.getId(), 5L);

		// then
		assertEquals(4L, topReviewKeywords.getTotalCount());
		assertThat(topReviewKeywords.getKeywords()).hasSize(2)
			.extracting("keywordId", "keyword", "selectionCount")
			.containsExactly(
				tuple(keywords.get(1).getId(), keywords.get(1).getContent(), 2L),
				tuple(keywords.get(0).getId(), keywords.get(0).getContent(), 2L)
			);
	}

	@DisplayName("리뷰 신고 요청 - 성공")
	@Test
	void createFestivalRequest_Success() {
		// given
		Review review = reviewRepository.save(createReview());

		ReviewReportRequest request = ReviewReportRequest.builder()
			.description("신고 사유")
			.build();

		// when
		ReviewReportResponse response = reviewService.createReviewReport(user, review.getId(), request);

		// then
		assertTrue(reviewReportRepository.existsById(response.getReportId()));
		assertThat(response)
			.extracting("reportId", "reviewId", "isPending")
			.contains(response.getReportId(), review.getId(), true);
	}

	@DisplayName("리뷰 신고 요청 - 존재하지 않는 리뷰일 경우 실패한다.")
	@Test
	void createFestivalRequest_NotExistingReview() {
		// given
		ReviewReportRequest request = ReviewReportRequest.builder()
			.description("신고 사유")
			.build();

		Long INVALID_REVIEW_ID = -1L;

		// when // then
		assertThatThrownBy(() -> reviewService.createReviewReport(user, INVALID_REVIEW_ID, request))
			.isInstanceOf(CustomException.class)
			.hasMessage(REVIEW_NOT_FOUND.getMessage());
	}

	private ReviewLike createReviewLike(Long userId, Long reviewId) {
		return ReviewLike.builder()
			.userId(userId)
			.reviewId(reviewId)
			.build();
	}

	private Review createReview() {
		return Review.builder()
			.userId(user.getId())
			.festivalId(festival.getId())
			.rating(35)
			.content("리뷰 내용")
			.build();
	}

	private ReviewKeyword createReviewKeyword(Long reviewId, Long keywordId) {
		return ReviewKeyword.builder()
			.reviewId(reviewId)
			.keywordId(keywordId)
			.build();
	}

	private ReviewKeyword createReviewKeyword(Long reviewId, Long keywordId, LocalDateTime createdAt) {
		return ReviewKeyword.builder()
			.reviewId(reviewId)
			.keywordId(keywordId)
			.createdAt(createdAt)
			.build();
	}
}
