package com.odiga.fiesta.review.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.odiga.fiesta.review.dto.projection.ReviewDataWithLike;
import com.odiga.fiesta.review.dto.response.ReviewImageResponse;
import com.odiga.fiesta.review.dto.response.ReviewKeywordResponse;

public interface ReviewCustomRepository {

	Page<ReviewDataWithLike> findReviews(Long userId, Long festivalId, Pageable pageable);

	Map<Long, List<ReviewKeywordResponse>> findReviewKeywordsMap(List<Long> reviewIds);

	Map<Long, List<ReviewImageResponse>> findReviewImagesMap(List<Long> reviewIds);
}
