package com.odiga.fiesta.review.service;

import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.repository.FestivalRepository;
import com.odiga.fiesta.keyword.domain.Keyword;
import com.odiga.fiesta.keyword.repository.KeywordRepository;
import com.odiga.fiesta.review.domain.Review;
import com.odiga.fiesta.review.domain.ReviewImage;
import com.odiga.fiesta.review.domain.ReviewKeyword;
import com.odiga.fiesta.review.dto.ReviewResponse;
import com.odiga.fiesta.review.repository.ReviewImageRepository;
import com.odiga.fiesta.review.repository.ReviewKeywordRepository;
import com.odiga.fiesta.review.repository.ReviewLikeRepository;
import com.odiga.fiesta.review.repository.ReviewRepository;
import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.odiga.fiesta.common.error.ErrorCode.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final FestivalRepository festivalRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewKeywordRepository reviewKeywordRepository;
    private final KeywordRepository keywordRepository;

    // 리뷰 TOP3 조회
    public ReviewResponse.getTop3ReviewsDTO getTop3ReviewsDTO() {
        List<Object[]> reviewsWithLikes = reviewLikeRepository.findReviewsWithLikeCount();

        // 좋아요 수에 따라 상위 3개의 리뷰를 필터링하고, 리뷰 정보를 DTO로 변환
        List<ReviewResponse.topReviewInfo> topReviewInfos = reviewsWithLikes.stream()
                .limit(3)
                .map(result -> {
                    Long reviewId = ((Number) result[0]).longValue();

                    // 리뷰 조회
                    Review review = reviewRepository.findById(reviewId)
                            .orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

                    // 페스티벌 조회
                    Festival festival = festivalRepository.findById(review.getFestivalId())
                            .orElseThrow(() -> new CustomException(FESTIVAL_NOT_FOUND));

                    // 리뷰 이미지 조회
                    List<String> imageUrls = getReviewImageUrls(reviewId);
                    String firstImageUrl = imageUrls.isEmpty() ? null : imageUrls.get(0);

                    // 리뷰 키워드 조회
                    List<String> reviewKeywords = getReviewKeywords(reviewId);

                    return ReviewResponse.topReviewInfo.builder()
                            .reviewId(reviewId)
                            .festivalId(review.getFestivalId())
                            .festivalName(festival.getName())
                            .content(review.getContent())
                            .rating(review.getScore())
                            .images(firstImageUrl)  // 첫 번째 이미지 URL
                            .keywords(reviewKeywords)
                            .build();
                })
                .collect(Collectors.toList());

        return ReviewResponse.getTop3ReviewsDTO.builder()
                .content(topReviewInfos)
                .build();
    }

    // 리뷰 다건 조회
    public ReviewResponse.getReviewsDTO getReviews(Long userId, Long festivalId, String sort, int page, int size) {

        // 유저 조회
        log.info("userId: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // 페스티벌 조회
        log.info("festivalId: {}", festivalId);
        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new CustomException(FESTIVAL_NOT_FOUND));

        // 기본 정렬 기준 설정 (최신순)
        Sort sortBy = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sortBy);
        Page<Review> reviewPage = reviewRepository.findAllByFestivalId(festivalId, pageable);

        List<ReviewResponse.reviewInfo> reviews = reviewPage.getContent().stream()
                .map(review -> {
                    // 리뷰 이미지 조회
                    List<String> imageUrls = getReviewImageUrls(review.getId());

                    // 리뷰 키워드 조회
                    List<String> reviewKeywords = getReviewKeywords(review.getId());

                    // 리뷰 좋아요 조회
                    int likes = getReviewLikesCount(review.getId());
                    boolean liked = hasUserLikedReview(review.getId(), userId);

                    // 리뷰 작성자 조회
                    User reviewer = userRepository.findById(review.getUserId())
                            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

                    return ReviewResponse.reviewInfo.builder()
                            .reviewId(review.getId())
                            .nickname(reviewer.getNickname())
                            .content(review.getContent())
                            .date(review.getCreatedAt().toLocalDate())
                            .rating(review.getScore())
                            .images(imageUrls)
                            .keywords(reviewKeywords)
                            .liked(liked)
                            .likes(likes)
                            .writtenByMe(reviewer.equals(user))
                            .build();
                })
                .sorted((r1, r2) -> {
                    if ("recommend".equals(sort)) {
                        return Integer.compare(r2.getLikes(), r1.getLikes()); // 좋아요 수로 정렬
                    } else {
                        return r2.getDate().compareTo(r1.getDate()); // 최신순 정렬
                    }
                })
                .toList();

        return ReviewResponse.getReviewsDTO.builder()
                .content(reviews)
                .offset(pageable.getPageNumber() * pageable.getPageSize())
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .build();
    }

    // 리뷰 이미지 조회
    private List<String> getReviewImageUrls(Long reviewId) {
        return reviewImageRepository.findByReviewId(reviewId).stream()
                .map(ReviewImage::getImageUrl)
                .toList();
    }

    // 리뷰 키워드 조회
    private List<String> getReviewKeywords(Long reviewId) {
        return reviewKeywordRepository.findByReviewId(reviewId).stream()
                .map(ReviewKeyword::getKeywordId)
                .distinct() // 중복된 ID 제거 (필요한 경우)
                .map(keywordRepository::findById)
                .flatMap(Optional::stream) // Optional을 stream으로 변환 (존재할 경우)
                .map(Keyword::getKeyword)
                .toList();
    }

    // 리뷰 좋아요 수 조회
    private int getReviewLikesCount(Long reviewId) {
        return reviewLikeRepository.findByReviewId(reviewId).size();
    }

    // 리뷰 좋아요 여부 조회
    private boolean hasUserLikedReview(Long reviewId, Long userId) {
        return reviewLikeRepository.findByReviewId(reviewId).stream()
                .anyMatch(like -> like.getUserId().equals(userId));
    }
}
