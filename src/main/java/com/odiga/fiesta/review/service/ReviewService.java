package com.odiga.fiesta.review.service;

import static com.odiga.fiesta.common.error.ErrorCode.*;
import static java.util.Objects.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.odiga.fiesta.badge.service.BadgeService;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.common.util.FileUtils;
import com.odiga.fiesta.festival.repository.FestivalRepository;
import com.odiga.fiesta.keyword.domain.Keyword;
import com.odiga.fiesta.keyword.repository.KeywordRepository;
import com.odiga.fiesta.review.domain.Review;
import com.odiga.fiesta.review.domain.ReviewImage;
import com.odiga.fiesta.review.domain.ReviewKeyword;
import com.odiga.fiesta.review.domain.ReviewReport;
import com.odiga.fiesta.review.dto.projection.ReviewData;
import com.odiga.fiesta.review.dto.projection.ReviewDataWithLike;
import com.odiga.fiesta.review.dto.projection.ReviewSimpleData;
import com.odiga.fiesta.review.dto.request.ReviewCreateRequest;
import com.odiga.fiesta.review.dto.request.ReviewReportRequest;
import com.odiga.fiesta.review.dto.request.ReviewUpdateRequest;
import com.odiga.fiesta.review.dto.response.ReviewIdResponse;
import com.odiga.fiesta.review.dto.response.ReviewImageResponse;
import com.odiga.fiesta.review.dto.response.ReviewKeywordResponse;
import com.odiga.fiesta.review.dto.response.ReviewReportResponse;
import com.odiga.fiesta.review.dto.response.ReviewResponse;
import com.odiga.fiesta.review.dto.response.ReviewSimpleResponse;
import com.odiga.fiesta.review.dto.response.TopReviewKeywordsResponse;
import com.odiga.fiesta.review.repository.ReviewImageRepository;
import com.odiga.fiesta.review.repository.ReviewKeywordRepository;
import com.odiga.fiesta.review.repository.ReviewLikeRepository;
import com.odiga.fiesta.review.repository.ReviewReportRepository;
import com.odiga.fiesta.review.repository.ReviewRepository;
import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

	private static final String REVIEW_DIR_NAME = "review";

	private final ReviewImageRepository reviewImageRepository;
	private final ReviewRepository reviewRepository;
	private final FestivalRepository festivalRepository;
	private final ReviewKeywordRepository reviewKeywordRepository;
	private final KeywordRepository keywordRepository;
	private final UserRepository userRepository;
	private final ReviewReportRepository reviewReportRepository;
	private final ReviewLikeRepository reviewLikeRepository;
	private final BadgeService badgeService;

	private final FileUtils fileUtils;

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
		validateFestival(festivalId);
		return reviewRepository.findTopReviewKeywords(festivalId, size);
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

	@Transactional
	public ReviewIdResponse createReview(final Long userId, final ReviewCreateRequest request,
		final List<MultipartFile> images) {
		validateUserId(userId);
		validateFileCount(images);
		validateFileExtension(images);
		validateReviewKeyword(request.getKeywordIds());

		Review review = reviewRepository.save(Review.createReview(userId, request));
		createReviewImages(images, review);
		saveReviewKeywords(request.getKeywordIds(), review);

		badgeService.giveReviewBadge(userId);

		return ReviewIdResponse.builder()
			.reviewId(review.getId())
			.build();
	}

	@Transactional
	public ReviewIdResponse updateReview(Long userId, Long reviewId, ReviewUpdateRequest request,
		List<MultipartFile> images) {

		validateMyReview(userId, reviewId);
		validateDeletedImages(request.getDeletedImages(), reviewId);

		long existingImageCount = reviewImageRepository.countByReviewId(reviewId);
		long newImageCount = (images == null) ? 0L : images.size();
		long deletedImageCount = (request.getDeletedImages() == null) ? 0L : request.getDeletedImages().size();

		long totalImageCount = existingImageCount + newImageCount - deletedImageCount;

		if (totalImageCount > 3) {
			throw new CustomException(REVIEW_IMAGE_COUNT_EXCEEDED);
		}

		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

		// 리뷰 내용 수정
		updateReviewContent(request, review);

		// 리뷰 키워드 수정
		updateReviewKeywords(request, review);

		// 리뷰 이미지 수정
		updateReviewImages(request, images, review);

		return ReviewIdResponse.builder()
			.reviewId(review.getId())
			.build();
	}

	private void updateReviewContent(ReviewUpdateRequest request, Review review) {
		review.updateRating((int)(request.getRating() * 10));
		review.updateContent(request.getContent());
	}

	private void updateReviewKeywords(ReviewUpdateRequest request, Review review) {
		reviewKeywordRepository.deleteByReviewId(review.getId());
		saveReviewKeywords(request.getKeywordIds(), review);
	}

	private void updateReviewImages(ReviewUpdateRequest request, List<MultipartFile> images, Review review) {
		// 삭제된 이미지를 S3에서 제거한 후 DB에서 제거
		if (request.getDeletedImages() != null && !request.getDeletedImages().isEmpty()) {
			// 먼저 삭제할 이미지들의 URL을 DB에서 조회
			List<String> imageUrlsToDelete = reviewImageRepository.findImageUrlByIdIn(request.getDeletedImages());

			// S3에서 이미지 파일 삭제
			imageUrlsToDelete.forEach(imageUrl -> {
				try {
					fileUtils.removeFile(imageUrl, REVIEW_DIR_NAME);
				} catch (Exception e) {
					log.error("Failed to delete image from S3: " + imageUrl, e);
				}
			});

			// S3에서 성공적으로 삭제된 후, DB에서 해당 이미지 정보 삭제
			reviewImageRepository.deleteByIdIn(request.getDeletedImages());
		}

		// 새로 추가된 이미지를 DB에 저장
		createReviewImages(images, review);
	}

	@Transactional
	public ReviewIdResponse deleteReview(Long userId, Long reviewId) {

		validateMyReview(userId, reviewId);

		// 1. 리뷰 및 연관 엔티티 삭제
		deleteReviewEntities(reviewId);

		// 2. 리뷰 이미지 파일 삭제 (트랜잭션 외부에서 처리)
		try {
			deleteReviewImages(reviewId);
		} catch (RuntimeException e) {
			log.error("Failed to delete review images for reviewId: " + reviewId, e);
		}

		return ReviewIdResponse.builder()
			.reviewId(reviewId)
			.build();
	}

	private void deleteReviewEntities(Long reviewId) {
		reviewRepository.findById(reviewId).orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

		reviewImageRepository.deleteByReviewId(reviewId);
		reviewKeywordRepository.deleteByReviewId(reviewId);
		reviewLikeRepository.deleteByReviewId(reviewId);
		reviewRepository.deleteById(reviewId);
	}

	private void deleteReviewImages(Long reviewId) {
		reviewImageRepository.findImageUrlByReviewId(reviewId).forEach(
			imageUrl -> fileUtils.removeFile(imageUrl, REVIEW_DIR_NAME)
		);
	}

	private void validateFestival(Long festivalId) {
		if (!festivalRepository.existsById(festivalId)) {
			throw new CustomException(FESTIVAL_NOT_FOUND);
		}
	}

	private void saveReviewKeywords(List<Long> keywordIds, Review review) {
		reviewKeywordRepository.saveAll(keywordIds.stream()
			.map(keywordId -> ReviewKeyword.builder()
				.reviewId(review.getId())
				.keywordId(keywordId)
				.build())
			.toList());
	}

	private void createReviewImages(final List<MultipartFile> images, final Review review) {
		if (isNull(images)) {
			return;
		}

		images.forEach(image -> {
			try {
				String imageUrl = fileUtils.uploadImage(image, REVIEW_DIR_NAME);
				reviewImageRepository.save(ReviewImage.builder()
					.reviewId(review.getId())
					.imageUrl(imageUrl)
					.build());
			} catch (IOException e) {
				log.error("파일 업로드 실패");
				e.printStackTrace();
			}
		});
	}

	private void validateUserId(Long userId) {
		if (isNull(userId)) {
			throw new CustomException(UNAUTHENTICATED_USER);
		}

		userRepository.findById(userId).orElseThrow(() -> new CustomException(USER_NOT_FOUND));
	}

	private void validateFileCount(List<MultipartFile> files) {
		if (isNull(files)) {
			return;
		}

		if (files.size() > 3) {
			throw new CustomException(REVIEW_IMAGE_COUNT_EXCEEDED);
		}
	}

	private void validateFileExtension(List<MultipartFile> files) {
		if (nonNull(files)) {
			files.forEach(fileUtils::validateImageExtension);
		}
	}

	private void validateReviewKeyword(List<Long> keywordIds) {
		if (keywordIds == null || keywordIds.isEmpty()) {
			throw new CustomException(REVIEW_KEYWORD_IS_EMPTY);
		}

		Set<Long> uniqueIds = new HashSet<>(keywordIds);
		if (uniqueIds.size() != keywordIds.size()) {
			throw new CustomException(KEYWORD_IS_DUPLICATED);
		}

		List<Keyword> validKeywords = keywordRepository.findAllById(keywordIds);
		if (validKeywords.size() != keywordIds.size()) {
			throw new CustomException(REVIEW_KEYWORD_NOT_FOUND);
		}
	}

	private void validateDeletedImages(List<Long> imageIds, Long reviewId) {
		if (isNull(imageIds) || imageIds.isEmpty()) {
			return;
		}

		long count = reviewImageRepository.countByIdInAndReviewId(imageIds, reviewId);

		if (count != imageIds.size()) {
			throw new CustomException(REVIEW_IMAGE_NOT_FOUND);
		}
	}

	private void validateMyReview(Long userId, Long reviewId) {
		Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

		if (!review.getUserId().equals(userId)) {
			throw new CustomException(REVIEW_NOT_MINE);
		}
	}

	private void validateReview(Long reviewId) {
		if (!reviewRepository.existsById(reviewId)) {
			throw new CustomException(REVIEW_NOT_FOUND);
		}
	}

	public ReviewReportResponse createReviewReport(User user, Long reviewId, ReviewReportRequest request) {

		validateUserId(user.getId());
		validateReview(reviewId);

		ReviewReport reviewReport = ReviewReport.builder()
			.reviewId(reviewId)
			.userId(user.getId())
			.description(request.getDescription())
			.isPending(true)
			.build();

		reviewReportRepository.save(reviewReport);

		return ReviewReportResponse.builder()
			.reportId(reviewReport.getId())
			.reviewId(reviewId)
			.isPending(reviewReport.getIsPending())
			.createdAt(reviewReport.getCreatedAt())
			.build();
	}
}
