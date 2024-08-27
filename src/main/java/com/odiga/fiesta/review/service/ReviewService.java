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
import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.repository.FestivalRepository;
import com.odiga.fiesta.keyword.domain.Keyword;
import com.odiga.fiesta.keyword.repository.KeywordRepository;
import com.odiga.fiesta.review.domain.Review;
import com.odiga.fiesta.review.domain.ReviewKeyword;
import com.odiga.fiesta.review.dto.projection.ReviewData;
import com.odiga.fiesta.review.dto.projection.ReviewDataWithLike;
import com.odiga.fiesta.review.dto.response.ReviewImageResponse;
import com.odiga.fiesta.review.dto.response.ReviewKeywordResponse;
import com.odiga.fiesta.review.dto.response.ReviewResponse;
import com.odiga.fiesta.review.dto.response.TopReviewResponse;
import com.odiga.fiesta.review.repository.ReviewImageRepository;
import com.odiga.fiesta.review.repository.ReviewKeywordRepository;
import com.odiga.fiesta.review.repository.ReviewLikeRepository;
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
	private final ReviewImageRepository reviewImageRepository;
	private final ReviewLikeRepository reviewLikeRepository;
	private final ReviewKeywordRepository reviewKeywordRepository;
	private final KeywordRepository keywordRepository;

	// 리뷰 TOP3 조회
	public List<TopReviewResponse> getTop3Reviews() {
		List<Object[]> reviewsWithLikes = reviewLikeRepository.findReviewsWithLikeCount();

		// 좋아요 수에 따라 상위 3개의 리뷰를 필터링하고, 리뷰 정보를 DTO로 변환
		return reviewsWithLikes.stream()
			.limit(3)
			.map(result -> {
				Long reviewId = ((Number)result[0]).longValue();

				// 리뷰 조회
				Review review = reviewRepository.findById(reviewId)
					.orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

				// 페스티벌 조회
				Festival festival = festivalRepository.findById(review.getFestivalId())
					.orElseThrow(() -> new CustomException(FESTIVAL_NOT_FOUND));

				// 리뷰 이미지 조회
				List<ReviewImageResponse> imageUrls = getReviewImageUrls(reviewId);
				ReviewImageResponse firstImageUrl = imageUrls.isEmpty() ? null : imageUrls.get(0);

				// 리뷰 키워드 조회
				List<ReviewKeywordResponse> reviewKeywords = getReviewKeywords(reviewId);

				return TopReviewResponse.builder()
					.reviewId(reviewId)
					.festivalId(review.getFestivalId())
					.festivalName(festival.getName())
					.content(review.getContent())
					.rating(review.getRating())
					.images(firstImageUrl)  // 첫 번째 이미지 URL
					.keywords(reviewKeywords)
					.build();
			})
			.collect(Collectors.toList());
	}

	// 가장 많이 선택된 키워드 TOP5 조회
	public List<ReviewKeywordResponse> getTop5Keywords(Long festivalId) {

		// 페스티벌 조회
		Festival festival = festivalRepository.findById(festivalId)
			.orElseThrow(() -> new CustomException(FESTIVAL_NOT_FOUND));

		// 리뷰 조회
		List<Review> reviews = reviewRepository.findAllByFestivalId(festivalId);

		// 리뷰 내 키워드 수집
		List<ReviewKeyword> reviewKeywords = reviews.stream()
			.flatMap(review -> reviewKeywordRepository.findByReviewId(review.getId()).stream())
			.toList();

		// 키워드 빈도수 집계
		Map<Long, Long> keywordFrequencyMap = reviewKeywords.stream()
			.collect(Collectors.groupingBy(
				ReviewKeyword::getKeywordId,
				Collectors.counting()
			));

		// 최신 타임스탬프
		Map<Long, LocalDateTime> keywordLatestTimestampMap = reviewKeywords.stream()
			.collect(Collectors.toMap(
				ReviewKeyword::getKeywordId,
				ReviewKeyword::getCreatedAt,
				(existing, replacement) -> existing.isAfter(replacement) ? existing : replacement
			));

		// 빈도수와 최신 타임스탬프에 따라 정렬하여 상위 5개의 키워드 id 추출
		List<Long> topKeywordIds = keywordFrequencyMap.entrySet().stream()
			.sorted(Map.Entry.<Long, Long>comparingByValue(Comparator.reverseOrder())
				.thenComparing(entry -> keywordLatestTimestampMap.get(entry.getKey()), Comparator.reverseOrder()))
			.limit(5)
			.map(Map.Entry::getKey)
			.toList();

		return topKeywordIds.stream()
			.map(keywordId -> {
				Keyword keyword = keywordRepository.findById(keywordId)
					.orElseThrow(() -> new CustomException(KEYWORD_NOT_FOUND));

				return ReviewKeywordResponse.builder()
					.keywordId(keyword.getId())
					.keyword(keyword.getContent())
					.build();
			})
			.collect(Collectors.toList());
	}

	// 리뷰 이미지 조회
	private List<ReviewImageResponse> getReviewImageUrls(Long reviewId) {
		return reviewImageRepository.findByReviewId(reviewId).stream()
			.map(reviewImage -> ReviewImageResponse.builder()
				.imageId(reviewImage.getId())
				.imageUrl(reviewImage.getImageUrl())
				.build())
			.toList();
	}

	// 리뷰 키워드 조회
	private List<ReviewKeywordResponse> getReviewKeywords(Long reviewId) {

		return reviewKeywordRepository.findByReviewId(reviewId).stream()
			.map(reviewKeyword -> {
				Keyword keyword = keywordRepository.findById(reviewKeyword.getKeywordId())
					.orElseThrow(() -> new CustomException(KEYWORD_NOT_FOUND));

				return ReviewKeywordResponse.builder()
					.keywordId(keyword.getId())
					.keyword(keyword.getContent())
					.build();
			})
			.collect(Collectors.toList());
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
