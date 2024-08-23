package com.odiga.fiesta.review.repository;

import com.odiga.fiesta.review.domain.ReviewImage;
import com.odiga.fiesta.review.domain.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    List<ReviewImage> findByReviewId(Long reviewId);
}
