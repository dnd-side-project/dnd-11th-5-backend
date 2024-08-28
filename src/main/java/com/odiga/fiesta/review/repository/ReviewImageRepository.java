package com.odiga.fiesta.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.odiga.fiesta.review.domain.ReviewImage;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

	List<ReviewImage> findByReviewId(Long reviewId);

	Long countByIdInAndReviewId(List<Long> imageIds, Long reviewId);

	Long countByReviewId(Long reviewId);

	@Query("SELECT ri.imageUrl FROM ReviewImage ri WHERE ri.reviewId = :reviewId")
	List<String> findImageUrlByReviewId(@Param("reviewId") Long reviewId);

	@Query("SELECT ri.imageUrl FROM ReviewImage ri WHERE ri.id IN :imageIds")
	List<String> findImageUrlByIdIn(List<Long> imageIds);

	void deleteByIdIn(List<Long> imageIds);

	void deleteByReviewId(Long reviewId);
}
