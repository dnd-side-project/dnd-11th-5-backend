package com.odiga.fiesta.review.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.odiga.fiesta.RepositoryTestSupport;
import com.odiga.fiesta.keyword.domain.Keyword;
import com.odiga.fiesta.review.domain.Review;
import com.odiga.fiesta.review.domain.ReviewImage;
import com.odiga.fiesta.review.domain.ReviewKeyword;
import com.odiga.fiesta.review.domain.ReviewLike;
import com.odiga.fiesta.review.dto.projection.ReviewDataWithLike;
import com.odiga.fiesta.review.dto.response.ReviewImageResponse;
import com.odiga.fiesta.review.dto.response.ReviewKeywordResponse;
import com.odiga.fiesta.user.domain.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

class ReviewRepositoryTest extends RepositoryTestSupport {

	@Autowired
	private ReviewRepository reviewRepository;

	@PersistenceContext
	private EntityManager em;

	@DisplayName("리뷰 다건 조회 - 기본 정렬 (최신순)으로 리뷰를 조회한다.")
	@Test
	void findReviews_SortByCreatedAt() {
		// given
		Review review1 = createReview(LocalDateTime.of(2021, 1, 1, 0, 0));
		Review review2 = createReview(LocalDateTime.of(2021, 1, 2, 0, 0));
		em.persist(review1);
		em.persist(review2);

		// when
		Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
		List<ReviewDataWithLike> content = reviewRepository.findReviews(null, 1L, pageable).getContent();

		// then
		assertThat(content).hasSize(2);
		assertThat(content.get(0).getReviewId()).isEqualTo(review2.getId());
		assertThat(content.get(1).getReviewId()).isEqualTo(review1.getId());
	}

	@DisplayName("리뷰 다건 조회 - 좋아요 순으로 리뷰를 조회한다.")
	@Test
	void findReviews_SortByLikeCount() {
		// given
		final Long MY_USER_ID = 2L;

		Review review1 = createReview();
		Review review2 = createReview();

		em.persist(review1);
		em.persist(review2);

		ReviewLike reviewLike1 = createReviewLike(review1.getId(), 1L);
		ReviewLike reviewLike2 = createReviewLike(review1.getId(), MY_USER_ID);
		ReviewLike reviewLike3 = createReviewLike(review2.getId(), 1L);

		em.persist(reviewLike1);
		em.persist(reviewLike2);
		em.persist(reviewLike3);

		// when
		Pageable pageable = PageRequest.of(0, 10, Sort.by("likeCount").descending());
		List<ReviewDataWithLike> content = reviewRepository.findReviews(MY_USER_ID, 1L, pageable).getContent();

		// then
		assertThat(content).hasSize(2);
		assertThat(content.get(0).getReviewId()).isEqualTo(review1.getId());
		assertThat(content.get(1).getReviewId()).isEqualTo(review2.getId());
		assertEquals(content.get(0).getLikeCount(), 2L);
		assertEquals(content.get(1).getLikeCount(), 1L);
		assertTrue(content.get(0).getIsLiked());
	}

	@DisplayName("리뷰 다건 조회 - 리뷰 작성한 유저 확인")
	@Test
	void findReviews_CheckUser() {
		// given
		User user = createUser();
		em.persist(user);

		Review review = createReview(user.getId());
		em.persist(review);

		// when
		Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
		List<ReviewDataWithLike> content = reviewRepository.findReviews(user.getId(), 1L, pageable).getContent();

		// then
		assertThat(content).hasSize(1);
		assertThat(content.get(0).getUser().getUserId()).isEqualTo(user.getId());
		assertThat(content.get(0).getUser().getNickname()).isEqualTo(user.getNickname());
		assertThat(content.get(0).getUser().getProfileImage()).isEqualTo(user.getProfileImage());
	}

	@DisplayName("리뷰 키워드 조회 - 리뷰 id의 리스트를 통해, id로 그룹핑된 키워드를 조회한다.")
	@Test
	void findReviewKeywordsMap() {
		// given
		Review review = createReview();
		Keyword keyword1 = createKeyword();
		Keyword keyword2 = createKeyword();

		em.persist(review);
		em.persist(keyword1);
		em.persist(keyword2);

		ReviewKeyword reviewKeyword1 = createReviewKeyword(review.getId(), keyword1.getId());
		ReviewKeyword reviewKeyword2 = createReviewKeyword(review.getId(), keyword2.getId());

		em.persist(reviewKeyword1);
		em.persist(reviewKeyword2);

		// when
		Map<Long, List<ReviewKeywordResponse>> keywords = reviewRepository.findReviewKeywordsMap(
			List.of(review.getId()));

		// then
		assertThat(keywords).hasSize(1);
		assertThat(keywords.get(review.getId())).hasSize(2);

	}

	@DisplayName("리뷰 이미지 조회 - 이미지가 없는 경우 빈 리스트로 반환된다.")
	@Test
	void findReviewImagesMap() {
		// given
		Review review1 = createReview();
		Review review2 = createReview();

		em.persist(review1);
		em.persist(review2);

		ReviewImage reviewImage1 = createReviewImage(review1.getId());
		ReviewImage reviewImage2 = createReviewImage(review1.getId());

		em.persist(reviewImage1);
		em.persist(reviewImage2);

		// when
		Map<Long, List<ReviewImageResponse>> reviewImagesMap = reviewRepository.findReviewImagesMap(
			List.of(review1.getId(), review2.getId()));

		// then
		assertThat(reviewImagesMap).hasSize(2);
		assertThat(reviewImagesMap.get(review1.getId())).hasSize(2);
		assertThat(reviewImagesMap.get(review2.getId())).hasSize(0);
	}

	static Review createReview() {
		return Review.builder()
			.userId(1L)
			.festivalId(1L)
			.rating(50)
			.content("리뷰 내용")
			.build();
	}

	static Review createReview(Long userId) {
		return Review.builder()
			.userId(userId)
			.festivalId(1L)
			.rating(50)
			.content("리뷰 내용")
			.build();
	}

	static Review createReview(LocalDateTime createdAt) {
		return Review.builder()
			.userId(1L)
			.festivalId(1L)
			.rating(50)
			.content("리뷰 내용")
			.createdAt(createdAt)
			.build();
	}

	static ReviewKeyword createReviewKeyword(Long reviewId, Long KeywordId) {
		return ReviewKeyword.builder()
			.reviewId(reviewId)
			.keywordId(KeywordId)
			.build();
	}

	static Keyword createKeyword() {
		return Keyword.builder()
			.content("키워드")
			.build();
	}

	static ReviewImage createReviewImage(Long reviewId) {
		return ReviewImage.builder()
			.reviewId(reviewId)
			.imageUrl("이미지URL")
			.build();
	}

	static ReviewLike createReviewLike(Long reviewId, Long userId) {
		return ReviewLike.builder()
			.reviewId(reviewId)
			.userId(userId)
			.build();
	}

	static User createUser() {
		return User.builder()
			.email("fiesta@odiga.com")
			.userTypeId(1L)
			.nickname("피에스타")
			.statusMessage("안녕하세요")
			.profileImage("프로필 이미지")
			.build();
	}
}
