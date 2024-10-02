package com.odiga.fiesta.review.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.odiga.fiesta.review.dto.projection.ReviewDataWithLike;
import com.odiga.fiesta.review.dto.projection.ReviewSimpleData;
import com.odiga.fiesta.review.dto.response.ReviewImageResponse;
import com.odiga.fiesta.review.dto.response.ReviewKeywordResponse;
import com.odiga.fiesta.review.dto.response.TopReviewKeywordsResponse;

public interface ReviewCustomRepository {

	Page<ReviewDataWithLike> findReviews(Long userId, Long festivalId, Pageable pageable);

	Map<Long, List<ReviewKeywordResponse>> findReviewKeywordsMap(List<Long> reviewIds);

	Map<Long, List<ReviewImageResponse>> findReviewImagesMap(List<Long> reviewIds);

	List<ReviewSimpleData> findMostLikeReviews(Long size);

	TopReviewKeywordsResponse findTopReviewKeywords(Long festivalId, Long size);

	Long countByUserIdAndCategoryId(Long userId, Long festivalCategoryId);

	Optional<ReviewDataWithLike> findReview(Long userId, Long reviewId);
}
