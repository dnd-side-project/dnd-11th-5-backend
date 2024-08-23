package com.odiga.fiesta.review.repository;

import com.odiga.fiesta.review.domain.ReviewKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewKeywordRepository extends JpaRepository<ReviewKeyword, Long> {

    List<ReviewKeyword> findByReviewId(Long reviewId);
}
