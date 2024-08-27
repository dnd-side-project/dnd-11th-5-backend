package com.odiga.fiesta.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.review.domain.ReviewImage;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    List<ReviewImage> findByReviewId(Long reviewId);
}
