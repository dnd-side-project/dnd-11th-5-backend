package com.odiga.fiesta.review.service;

import static com.odiga.fiesta.common.error.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.odiga.fiesta.MockTestSupport;
import com.odiga.fiesta.badge.service.BadgeService;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.common.util.FileUtils;
import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.repository.FestivalRepository;
import com.odiga.fiesta.keyword.domain.Keyword;
import com.odiga.fiesta.keyword.repository.KeywordRepository;
import com.odiga.fiesta.review.domain.Review;
import com.odiga.fiesta.review.domain.ReviewKeyword;
import com.odiga.fiesta.review.dto.projection.ReviewDataWithLike;
import com.odiga.fiesta.review.dto.request.ReviewCreateRequest;
import com.odiga.fiesta.review.dto.request.ReviewUpdateRequest;
import com.odiga.fiesta.review.dto.response.ReviewImageResponse;
import com.odiga.fiesta.review.dto.response.ReviewKeywordResponse;
import com.odiga.fiesta.review.dto.response.ReviewResponse;
import com.odiga.fiesta.review.dto.response.ReviewUserInfo;
import com.odiga.fiesta.review.repository.ReviewImageRepository;
import com.odiga.fiesta.review.repository.ReviewKeywordRepository;
import com.odiga.fiesta.review.repository.ReviewLikeRepository;
import com.odiga.fiesta.review.repository.ReviewRepository;
import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.repository.UserRepository;

class ReviewServiceMockTest extends MockTestSupport {

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private FestivalRepository festivalRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private KeywordRepository keywordRepository;

	@Mock
	private FileUtils fileUtils;

	@Mock
	private ReviewImageRepository reviewImageRepository;

	@Mock
	private ReviewKeywordRepository reviewKeywordRepository;

	@Mock
	private ReviewLikeRepository reviewLikeRepository;

	@Mock
	private BadgeService badgeService;
	
	@InjectMocks
	private ReviewService reviewService;

	@DisplayName("리뷰 다건 조회 - 별점은 일의 자릿수, 소숫점 단위로 표시한다.")
	@Test
	void getReviews_RatingShouldDouble() {
		// given
		Long userId = 1L;
		Long festivalId = 1L;
		Pageable pageable = PageRequest.of(0, 10);

		given(festivalRepository.existsById(festivalId)).willReturn(true);

		ReviewDataWithLike reviewData =
			ReviewDataWithLike.builder()
				.reviewId(1L)
				.festivalId(festivalId)
				.user(new ReviewUserInfo(1L, "profileImage", "nickname"))
				.content("content")
				.createdAt(LocalDateTime.of(2021, 1, 1, 0, 0))
				.updatedAt(LocalDateTime.of(2021, 1, 1, 0, 0))
				.rating(50)
				.build();

		Page<ReviewDataWithLike> reviewsPage = new PageImpl<>(Collections.singletonList(reviewData), pageable, 1);
		given(reviewRepository.findReviews(userId, festivalId, pageable)).willReturn(reviewsPage);

		Map<Long, List<ReviewImageResponse>> reviewImagesMap = new HashMap<>();
		reviewImagesMap.put(1L, Arrays.asList(new ReviewImageResponse(1L, "imageUrl")));
		given(reviewRepository.findReviewImagesMap(any())).willReturn(reviewImagesMap);

		Map<Long, List<ReviewKeywordResponse>> reviewKeywordsMap = new HashMap<>();
		reviewKeywordsMap.put(1L, Arrays.asList(new ReviewKeywordResponse(1L, "keyword")));
		given(reviewRepository.findReviewKeywordsMap(any())).willReturn(reviewKeywordsMap);

		// when
		Page<ReviewResponse> reviews = reviewService.getReviews(userId, festivalId, pageable);

		// then
		assertEquals(1, reviews.getTotalElements());
		ReviewResponse review = reviews.getContent().get(0);
		assertEquals(5.0, review.getRating());
	}

	@Nested
	@DisplayName("리뷰 생성")
	class ReviewCreationTest {

		User user = User.builder()
			.id(1L)
			.email("fiesta@odiga.com")
			.userTypeId(1L)
			.nickname("피에스타")
			.profileImage("profileImage")
			.statusMessage("상태메시지")
			.build();

		Festival festival = createFestival();

		Keyword keyword = Keyword.builder()
			.id(1L)
			.content("✨ 쾌적해요")
			.build();

		ReviewCreateRequest validRequest = ReviewCreateRequest.builder()
			.festivalId(festival.getId())
			.rating(1.5)
			.keywordIds(Collections.singletonList(keyword.getId()))
			.content("content")
			.build();

		Review review = Review.builder()
			.id(1L)
			.userId(user.getId())
			.festivalId(festival.getId())
			.rating((int)(validRequest.getRating() * 10))
			.content(validRequest.getContent())
			.build();

		List<MultipartFile> validImages = List.of(
			new MockMultipartFile(
				"test1",
				"test1.png",
				MULTIPART_FORM_DATA_VALUE,
				"test1".getBytes()),
			new MockMultipartFile(
				"test2",
				"test2.jpeg",
				MULTIPART_FORM_DATA_VALUE,
				"test2".getBytes()),
			new MockMultipartFile(
				"test3",
				"test3.jpg",
				MULTIPART_FORM_DATA_VALUE,
				"test3".getBytes())
		);

		@BeforeEach
		void setUp() {
			given(userRepository.findById(user.getId())).willReturn(Optional.ofNullable(user));
		}

		@DisplayName("성공")
		@Test
		void createReview_Success() {
			// given
			given(keywordRepository.findAllById(Collections.singletonList(keyword.getId()))).willReturn(
				Collections.singletonList(keyword));
			given(reviewRepository.save(any())).willReturn(review);

			// when
			reviewService.createReview(user.getId(), validRequest, validImages);

			// then
			then(reviewRepository).should().save(any());
		}

		@DisplayName("성공 - 이미지가 없을 때")
		@Test
		void createReview_SuccessWithNoImage() {
			// given
			given(keywordRepository.findAllById(Collections.singletonList(keyword.getId()))).willReturn(
				Collections.singletonList(keyword));
			given(reviewRepository.save(any())).willReturn(review);

			// when
			reviewService.createReview(user.getId(), validRequest, null);

			// then
			then(reviewRepository).should().save(any());
		}

		@DisplayName("성공 - 키워드에 중복되는게 있어도 하나만 저장")
		@Test
		void createReview_RemoveDuplicatedKeywords() {
			// given
			ReviewCreateRequest request = ReviewCreateRequest.builder()
				.festivalId(festival.getId())
				.rating(1.5)
				.keywordIds(List.of(keyword.getId(), keyword.getId(), keyword.getId()))
				.content("content")
				.build();

			given(keywordRepository.findAllById(Collections.singletonList(keyword.getId()))).willReturn(
				Collections.singletonList(keyword));
			given(reviewRepository.save(any())).willReturn(review);

			// when
			reviewService.createReview(user.getId(), request, null);

			// then
			ArgumentCaptor<List<ReviewKeyword>> reviewKeywordsCaptor = ArgumentCaptor.forClass(List.class);
			verify(reviewKeywordRepository).saveAll(reviewKeywordsCaptor.capture());

			List<ReviewKeyword> savedReviewKeywords = reviewKeywordsCaptor.getValue();
			assertNotNull(savedReviewKeywords);
			assertEquals(1, savedReviewKeywords.size());
			assertTrue(savedReviewKeywords.stream()
				.anyMatch(rk -> rk.getKeywordId().equals(keyword.getId())));
		}

		@DisplayName("실패 - 이미지 갯수 초과")
		@Test
		void createReview_ImageCountExceeded() {
			// given
			List<MultipartFile> invalidImages = List.of(
				new MockMultipartFile(
					"test1",
					"test1.png",
					MULTIPART_FORM_DATA_VALUE,
					"test1".getBytes()),
				new MockMultipartFile(
					"test2",
					"test2.jpeg",
					MULTIPART_FORM_DATA_VALUE,
					"test2".getBytes()),
				new MockMultipartFile(
					"test3",
					"test3.jpg",
					MULTIPART_FORM_DATA_VALUE,
					"test3".getBytes()),
				new MockMultipartFile(
					"test4",
					"test4.jpg",
					MULTIPART_FORM_DATA_VALUE,
					"test4".getBytes())
			);

			// when // then
			CustomException exception = assertThrows(CustomException.class, () -> {
				reviewService.createReview(1L, validRequest, invalidImages);
			});

			assertEquals(REVIEW_IMAGE_COUNT_EXCEEDED.getMessage(), exception.getMessage());
		}
	}

	@Nested
	@DisplayName("리뷰 수정")
	class ReviewUpdateTest {

		User user = User.builder()
			.id(1L)
			.email("fiesta@odiga.com")
			.userTypeId(1L)
			.nickname("피에스타")
			.profileImage("profileImage")
			.statusMessage("상태메시지")
			.build();

		Keyword keyword = Keyword.builder()
			.id(1L)
			.content("✨ 쾌적해요")
			.build();

		ReviewUpdateRequest validRequest = ReviewUpdateRequest.builder()
			.rating(1.5)
			.keywordIds(Collections.singletonList(keyword.getId()))
			.content("수정내용 ")
			.deletedImages(Collections.singletonList(1L))
			.build();

		Review originalReview = Review.builder()
			.id(1L)
			.userId(user.getId())
			.festivalId(1L)
			.rating((int)(validRequest.getRating() * 10))
			.content(validRequest.getContent())
			.build();

		List<MultipartFile> validImages = List.of(
			new MockMultipartFile(
				"test1",
				"test1.png",
				MULTIPART_FORM_DATA_VALUE,
				"test1".getBytes()),
			new MockMultipartFile(
				"test2",
				"test2.jpeg",
				MULTIPART_FORM_DATA_VALUE,
				"test2".getBytes()),
			new MockMultipartFile(
				"test3",
				"test3.jpg",
				MULTIPART_FORM_DATA_VALUE,
				"test3".getBytes())
		);

		@DisplayName("성공 - 삭제할 이미지가 없을 때")
		@Test
		void updateReview_SuccessWithNoRemoveImage() {
			// given
			given(reviewRepository.findById(originalReview.getId())).willReturn(Optional.ofNullable(originalReview));

			ReviewUpdateRequest request = ReviewUpdateRequest.builder()
				.rating(1.5)
				.keywordIds(Collections.singletonList(keyword.getId()))
				.content("수정내용")
				.deletedImages(Collections.emptyList())
				.build();

			// when
			reviewService.updateReview(user.getId(), originalReview.getId(), request, null);

			// then
			Optional<Review> optionalReview = reviewRepository.findById(originalReview.getId());
			assertTrue(optionalReview.isPresent());
			assertThat(optionalReview.get().getRating()).isEqualTo((int)(request.getRating() * 10));
			assertThat(optionalReview.get().getContent()).isEqualTo(request.getContent());
		}

		@DisplayName("실패 - 이미지 갯수 초과")
		@Test
		void updateReview_ReviewImageCountExceed() {
			// given
			given(reviewRepository.findById(originalReview.getId())).willReturn(Optional.ofNullable(originalReview));
			given(reviewImageRepository.countByReviewId(originalReview.getId())).willReturn(3L);

			ReviewUpdateRequest request = ReviewUpdateRequest.builder()
				.rating(1.5)
				.keywordIds(Collections.singletonList(keyword.getId()))
				.content("수정내용")
				.deletedImages(Collections.emptyList())
				.build();

			List<MultipartFile> images = List.of(
				new MockMultipartFile(
					"test1",
					"test1.png",
					MULTIPART_FORM_DATA_VALUE,
					"test1".getBytes())
			);

			// when // then
			assertThatThrownBy(() -> reviewService.updateReview(user.getId(), originalReview.getId(), request, images))
				.isInstanceOf(CustomException.class)
				.hasMessage(REVIEW_IMAGE_COUNT_EXCEEDED.getMessage());
		}
	}

	@DisplayName("리뷰 삭제 - 리뷰와 관련된 엔티티를 모두 삭제한다.")
	@Test
	void deleteReview_ValidReview_ShouldDeleteReviewAndRelatedEntities() {
		// given
		Long userId = 1L;
		Long reviewId = 1L;
		String REVIEW_DIR_NAME = "review";

		given(reviewRepository.findById(reviewId)).willReturn(
			Optional.of(Review.builder().id(reviewId).userId(userId).build()));
		given(reviewImageRepository.findImageUrlByReviewId(reviewId)).willReturn(List.of("image1.jpg", "image2.jpg"));

		// when
		reviewService.deleteReview(userId, reviewId);

		// then
		then(reviewRepository).should(times(2)).findById(reviewId);
		then(reviewRepository).should(times(1)).deleteById(reviewId);
		then(reviewImageRepository).should(times(1)).deleteByReviewId(reviewId);
		then(reviewKeywordRepository).should(times(1)).deleteByReviewId(reviewId);
		then(reviewLikeRepository).should(times(1)).deleteByReviewId(reviewId);
		then(fileUtils).should(times(1)).removeFile("image1.jpg", REVIEW_DIR_NAME);
		then(fileUtils).should(times(1)).removeFile("image2.jpg", REVIEW_DIR_NAME);
	}

	@DisplayName("리뷰 삭제 - 파일 삭제 중 예외 발생 시에도 데이터베이스 엔티티는 삭제한다.")
	@Test
	void deleteReview_ExceptionDuringFileDeletion_ShouldStillDeleteDatabaseEntries() {
		// given
		Long userId = 1L;
		Long reviewId = 1L;
		String REVIEW_DIR_NAME = "review";

		given(reviewRepository.findById(reviewId)).willReturn(
			Optional.of(Review.builder().id(reviewId).userId(userId).build()));
		given(reviewImageRepository.findImageUrlByReviewId(reviewId)).willReturn(List.of("image1.jpg", "image2.jpg"));
		willThrow(new RuntimeException("File deletion failed")).given(fileUtils)
			.removeFile(anyString(), eq(REVIEW_DIR_NAME));

		// when
		reviewService.deleteReview(userId, reviewId);

		// then
		then(reviewRepository).should(times(2)).findById(reviewId);
		then(reviewRepository).should(times(1)).deleteById(reviewId);
		then(reviewImageRepository).should(times(1)).deleteByReviewId(reviewId);
		then(reviewKeywordRepository).should(times(1)).deleteByReviewId(reviewId);
		then(reviewLikeRepository).should(times(1)).deleteByReviewId(reviewId);
	}

	@DisplayName("리뷰 삭제 - 자신의 리뷰가 아닌 경우 에러가 발생")
	@Test
	void deleteReview_NotMyReview() {
		// given
		Long userId = 1L;
		Long reviewId = 1L;
		given(reviewRepository.findById(reviewId)).willReturn(Optional.ofNullable(Review.builder().userId(2L).build()));

		// when // then
		CustomException exception = assertThrows(CustomException.class, () -> {
			reviewService.deleteReview(userId, reviewId);
		});

		assertThat(exception.getMessage()).isEqualTo(REVIEW_NOT_MINE.getMessage());
	}

	private static Festival createFestival() {
		return Festival.builder()
			.userId(1L)
			.name("페스티벌 이름")
			.description("페스티벌 설명")
			.startDate(LocalDate.of(2024, 10, 4))
			.endDate(LocalDate.of(2024, 10, 4))
			.address("주소")
			.latitude(35.1731)
			.longitude(129.0714)
			.sidoId(6L)
			.sigungu("해운대구")
			.playtime("플레이타임")
			.homepageUrl("홈페이지")
			.instagramUrl("인스타그램")
			.ticketLink("티켓링크")
			.fee("입장료")
			.tip("팁")
			.isPending(false)
			.build();
	}
}
