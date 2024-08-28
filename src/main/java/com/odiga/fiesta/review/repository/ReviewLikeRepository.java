package com.odiga.fiesta.review.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.review.domain.ReviewLike;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

	Long countByReviewId(Long reviewId);

	Optional<ReviewLike> findByUserIdAndReviewId(Long userId, Long reviewId);

	void deleteByReviewId(Long reviewId);
}
