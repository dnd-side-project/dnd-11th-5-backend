package com.odiga.fiesta.review.service;

import static com.odiga.fiesta.common.error.ErrorCode.*;
import static java.util.Objects.*;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.review.domain.ReviewLike;
import com.odiga.fiesta.review.dto.response.ReviewLikeResponse;
import com.odiga.fiesta.review.repository.ReviewLikeRepository;
import com.odiga.fiesta.review.repository.ReviewRepository;
import com.odiga.fiesta.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewLikeService {

	private final ReviewRepository reviewRepository;
	private final ReviewLikeRepository reviewLikeRepository;
	private final UserRepository userRepository;

	@Transactional
	public ReviewLikeResponse updateReviewLike(Long userId, Long reviewId) {
		validateUser(userId);
		validateReview(reviewId);

		Optional<ReviewLike> optionalReviewLike = reviewLikeRepository.findByUserIdAndReviewId(userId, reviewId);
		boolean isLiked = optionalReviewLike.isPresent();

		optionalReviewLike.ifPresentOrElse(reviewLikeRepository::delete,
			() -> reviewLikeRepository.save(ReviewLike.builder()
				.userId(userId)
				.reviewId(reviewId)
				.build())
		);

		Long likeCount = reviewLikeRepository.countByReviewId(reviewId);

		return ReviewLikeResponse.builder()
			.reviewId(reviewId)
			.isLiked(!isLiked)
			.likeCount(likeCount)
			.build();
	}

	private void validateReview(Long reviewId) {
		if (!reviewRepository.existsById(reviewId)) {
			throw new CustomException(REVIEW_NOT_FOUND);
		}
	}

	private void validateUser(Long userId) {
		if (isNull(userId)) {
			throw new CustomException(UNAUTHENTICATED_USER);
		}

		userRepository.findById(userId).orElseThrow(() -> new CustomException(USER_NOT_FOUND));
	}
}
