package com.odiga.fiesta.review.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.odiga.fiesta.IntegrationTestSupport;
import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.repository.FestivalRepository;
import com.odiga.fiesta.keyword.domain.Keyword;
import com.odiga.fiesta.keyword.repository.KeywordRepository;
import com.odiga.fiesta.review.domain.Review;
import com.odiga.fiesta.review.domain.ReviewLike;
import com.odiga.fiesta.review.dto.response.ReviewSimpleResponse;
import com.odiga.fiesta.review.repository.ReviewLikeRepository;
import com.odiga.fiesta.review.repository.ReviewRepository;
import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.repository.UserRepository;

public class ReviewServiceTest extends IntegrationTestSupport {

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

		System.out.println(mostLikeReviews);

		// then
		assertThat(mostLikeReviews).hasSize(3)
			.extracting("reviewId", "festivalId", "content", "rating")
			.containsExactly(
				tuple(reviewLike5.getId(), festival.getId(), "리뷰 내용", 3.5),
				tuple(reviewLike3.getId(), festival.getId(), "리뷰 내용", 3.5),
				tuple(reviewLike2.getId(), festival.getId(), "리뷰 내용", 3.5)
			);
	}

	private ReviewLike createReviewLike(Long userId, Long reviewId) {
		return ReviewLike.builder()
			.userId(userId)
			.reviewId(reviewId)
			.build();
	}

	private Review createReview() {
		return Review.builder()
			.userId(1L)
			.festivalId(festival.getId())
			.rating(35)
			.content("리뷰 내용")
			.build();
	}
}
