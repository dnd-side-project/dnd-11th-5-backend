package com.odiga.fiesta.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.odiga.fiesta.review.domain.ReviewLike;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    List<ReviewLike> findByReviewId(Long reviewId);

    @Query("SELECT rl.reviewId, COUNT(rl.id) as likeCount " +
            "FROM ReviewLike rl " +
            "GROUP BY rl.reviewId " +
            "ORDER BY likeCount DESC")
    List<Object[]> findReviewsWithLikeCount();
}
