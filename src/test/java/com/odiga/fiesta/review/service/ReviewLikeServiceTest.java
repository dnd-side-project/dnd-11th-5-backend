package com.odiga.fiesta.review.service;

import static com.odiga.fiesta.common.error.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.odiga.fiesta.IntegrationTestSupport;
import com.odiga.fiesta.review.domain.Review;
import com.odiga.fiesta.review.domain.ReviewLike;
import com.odiga.fiesta.review.dto.response.ReviewLikeResponse;
import com.odiga.fiesta.review.repository.ReviewLikeRepository;
import com.odiga.fiesta.review.repository.ReviewRepository;
import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.repository.UserRepository;

class ReviewLikeServiceTest extends IntegrationTestSupport {

	@Autowired
	private ReviewLikeService reviewLikeService;

	@Autowired
	private ReviewLikeRepository reviewLikeRepository;

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private UserRepository userRepository;

	@DisplayName("리뷰 좋아요 등록/해제 - 좋아요 등록")
	@Test
	void updateReviewLike_AddLike() {
		// given
		Review review = reviewRepository.save(createReview());
		User user = userRepository.save(createUser());

		// when
		ReviewLikeResponse reviewLikeResponse = reviewLikeService.updateReviewLike(user.getId(), review.getId());

		// then
		assertThat(reviewLikeResponse.getIsLiked()).isTrue();
		assertEquals(1, reviewLikeResponse.getLikeCount());
	}

	@DisplayName("리뷰 좋아요 등록/해제 - 좋아요 해제")
	@Test
	void updateReviewLike_RemoveLike() {
		// given
		Review review = reviewRepository.save(createReview());
		User user = userRepository.save(createUser());
		ReviewLike reviewLike = createReviewLike(user, review);
		reviewLikeRepository.save(reviewLike);

		// when
		ReviewLikeResponse reviewLikeResponse = reviewLikeService.updateReviewLike(user.getId(), review.getId());

		// then
		assertThat(reviewLikeResponse.getIsLiked()).isFalse();
		assertEquals(0, reviewLikeResponse.getLikeCount());
	}

	@DisplayName("리뷰 좋아요 등록/해제 - 존재하지 않는 유저의 경우 에러 발생")
	@Test
	void updateReviewLike_UserNotFound() {
		// given
		Review review = reviewRepository.save(createReview());

		User deletedUser = userRepository.save(createUser());
		userRepository.delete(deletedUser);

		// when // then
		assertThatThrownBy(() -> reviewLikeService.updateReviewLike(deletedUser.getId(), review.getId()))
			.hasMessage(USER_NOT_FOUND.getMessage());
	}

	@DisplayName("리뷰 좋아요 등록/해제 - 존재하지 않는 리뷰에 좋아요 등록 시 에러 발생")
	@Test
	void updateReviewLike_ReviewNotFound() {
		// given
		User user = userRepository.save(createUser());

		// when // then
		assertThatThrownBy(() -> reviewLikeService.updateReviewLike(user.getId(), -1L))
			.hasMessage(REVIEW_NOT_FOUND.getMessage());
	}

	private static ReviewLike createReviewLike(User user, Review review) {
		return ReviewLike.builder()
			.userId(user.getId())
			.reviewId(review.getId())
			.build();
	}

	private static Review createReview() {
		return Review.builder()
			.userId(1L)
			.festivalId(1L)
			.rating(5)
			.content("리뷰 내용")
			.build();
	}

	private User createUser() {
		return User.builder()
			.userTypeId(1L)
			.nickname("테스트 유저")
			.statusMessage("상태 메시지")
			.profileImage("프로필 이미지 링크")
			.build();
	}

}
