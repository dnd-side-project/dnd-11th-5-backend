package com.odiga.fiesta.review.service;

import static com.odiga.fiesta.common.error.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.festival.repository.FestivalRepository;
import com.odiga.fiesta.keyword.domain.Keyword;
import com.odiga.fiesta.keyword.repository.KeywordRepository;
import com.odiga.fiesta.review.dto.projection.ReviewData;
import com.odiga.fiesta.review.dto.projection.ReviewDataWithLike;
import com.odiga.fiesta.review.dto.projection.ReviewSimpleData;
import com.odiga.fiesta.review.dto.response.ReviewImageResponse;
import com.odiga.fiesta.review.dto.response.ReviewKeywordResponse;
import com.odiga.fiesta.review.dto.response.ReviewResponse;
import com.odiga.fiesta.review.dto.response.ReviewSimpleResponse;
import com.odiga.fiesta.review.dto.response.TopReviewKeywordsResponse;
import com.odiga.fiesta.review.repository.ReviewKeywordRepository;
import com.odiga.fiesta.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final FestivalRepository festivalRepository;
	private final ReviewKeywordRepository reviewKeywordRepository;
	private final KeywordRepository keywordRepository;

	public List<ReviewKeywordResponse> getKeywords() {
		List<Keyword> keywords = keywordRepository.findAll();
		return keywords.stream()
			.map(ReviewKeywordResponse::of)
			.toList();
	}

	public List<ReviewSimpleResponse> getMostLikeReviews(Long size) {
		List<ReviewSimpleData> mostLikeReviews = reviewRepository.findMostLikeReviews(size);

		List<Long> reviewIds = mostLikeReviews.stream().map(ReviewSimpleData::getReviewId).toList();
		Map<Long, List<ReviewImageResponse>> reviewImagesMap = reviewRepository.findReviewImagesMap(reviewIds);
		Map<Long, List<ReviewKeywordResponse>> reviewKeywordsMap = reviewRepository.findReviewKeywordsMap(reviewIds);

		return mostLikeReviews.stream().map(review -> {
			List<ReviewImageResponse> images = reviewImagesMap.get(review.getReviewId());
			List<ReviewKeywordResponse> keywords = reviewKeywordsMap.get(review.getReviewId());

			return ReviewSimpleResponse.of(review, images.isEmpty() ? null : images.getFirst().getImageUrl(), keywords);
		}).toList();
	}

	public TopReviewKeywordsResponse getTopReviewKeywords(Long festivalId, Long size) {
		TopReviewKeywordsResponse topReviewKeywords = reviewRepository.findTopReviewKeywords(festivalId, size);
		return topReviewKeywords;
	}

	public Page<ReviewResponse> getReviews(Long userId, Long festivalId, Pageable pageable) {

		validateFestival(festivalId);
		Page<ReviewDataWithLike> reviews = reviewRepository.findReviews(userId, festivalId, pageable);

		List<Long> reviewIds = reviews.stream().map(ReviewData::getReviewId).toList();
		Map<Long, List<ReviewImageResponse>> reviewImagesMap = reviewRepository.findReviewImagesMap(reviewIds);
		Map<Long, List<ReviewKeywordResponse>> reviewKeywordsMap = reviewRepository.findReviewKeywordsMap(reviewIds);

		List<ReviewResponse> content = reviews.map(review -> {
			List<ReviewImageResponse> images = reviewImagesMap.get(review.getReviewId());
			List<ReviewKeywordResponse> keywords = reviewKeywordsMap.get(review.getReviewId());

			return ReviewResponse.of(review, images, keywords);
		}).toList();

		return new PageImpl<>(content, pageable, reviews.getTotalElements());
	}

	private void validateFestival(Long festivalId) {
		if (!festivalRepository.existsById(festivalId)) {
			throw new CustomException(FESTIVAL_NOT_FOUND);
		}
	}

}
