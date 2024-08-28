package com.odiga.fiesta.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.review.domain.ReviewKeyword;

public interface ReviewKeywordRepository extends JpaRepository<ReviewKeyword, Long> {

	List<ReviewKeyword> findByReviewId(Long reviewId);

	void deleteByReviewId(Long reviewId);
}
