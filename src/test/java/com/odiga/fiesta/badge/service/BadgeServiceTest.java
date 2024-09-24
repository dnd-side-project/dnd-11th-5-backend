package com.odiga.fiesta.badge.service;

import static com.odiga.fiesta.badge.domain.BadgeConstants.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.odiga.fiesta.IntegrationTestSupport;
import com.odiga.fiesta.badge.domain.Badge;
import com.odiga.fiesta.badge.domain.BadgeType;
import com.odiga.fiesta.badge.domain.UserBadge;
import com.odiga.fiesta.badge.repository.BadgeRepository;
import com.odiga.fiesta.badge.repository.UserBadgeRepository;
import com.odiga.fiesta.category.domain.Category;
import com.odiga.fiesta.category.domain.CategoryConstants;
import com.odiga.fiesta.category.repository.CategoryRepository;
import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.domain.FestivalCategory;
import com.odiga.fiesta.festival.repository.FestivalCategoryRepository;
import com.odiga.fiesta.festival.repository.FestivalRepository;
import com.odiga.fiesta.review.domain.Review;
import com.odiga.fiesta.review.repository.ReviewRepository;
import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.repository.UserRepository;

class BadgeServiceTest extends IntegrationTestSupport {

	@Autowired
	private BadgeService badgeService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BadgeRepository badgeRepository;

	@Autowired
	private UserBadgeRepository userBadgeRepository;

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private FestivalRepository festivalRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private FestivalCategoryRepository festivalCategoryRepository;

	@BeforeEach
	void setUp() {
		// 기본 뱃지 데이터 셋업
		badgeRepository.save(createBadge(1L, BadgeType.USER));
		badgeRepository.save(createBadge(2L, BadgeType.REVIEW));
		badgeRepository.save(createBadge(3L, BadgeType.FESTIVAL));

		for (long badgeId = 4L; badgeId <= 15L; badgeId++) {
			badgeRepository.save(createBadge(badgeId, BadgeType.REVIEW));
		}

		// 카테고리 셋업
		for (long categoryId = 1L; categoryId <= 12L; categoryId++) {
			categoryRepository.save(createCategory(categoryId));
		}
	}

	@AfterEach
	void tearDown() {
		userBadgeRepository.deleteAll();
		badgeRepository.deleteAll();
		userRepository.deleteAll();
		reviewRepository.deleteAll();
		festivalRepository.deleteAll();
		categoryRepository.deleteAll();
		festivalCategoryRepository.deleteAll();
	}

	@DisplayName("뱃지 수여 - 이미 뱃지를 가지고 있는 경우, 뱃지 수여하지 않음")
	@Test
	void giveUserBadge_ExistingUser() throws ExecutionException, InterruptedException {
		// given
		User user = createUser();
		userRepository.save(user);

		userBadgeRepository.save(UserBadge.builder()
			.userId(user.getId())
			.badgeId(1L)
			.build());

		// when
		CompletableFuture<List<Long>> badgeIdsFuture = badgeService.giveUserBadge(user.getId());
		List<Long> badgeIds = badgeIdsFuture.get();

		// then
		assertEquals(0, badgeIds.size());
	}

	// 유저 뱃지 수여 테스트
	@DisplayName("유저 뱃지 수여 - 회원가입 시 뱃지 수여")
	@Test
	void giveUserBadge_FirstJoin() throws ExecutionException, InterruptedException {
		// given
		User newUser = createUser();
		userRepository.save(newUser);

		// when
		CompletableFuture<List<Long>> badgeIdsFuture = badgeService.giveUserBadge(newUser.getId());
		List<Long> badgeIds = badgeIdsFuture.get();

		// then
		assertEquals(1, badgeIds.size());
		assertTrue(badgeIds.contains(USER_JOIN_BADGE_ID));
	}

	@DisplayName("리뷰 뱃지 수여 - 첫  리뷰  작성")
	@Test
	void giveReviewBadge_FirstReview() throws ExecutionException, InterruptedException {
		// given
		User user = createUser();
		userRepository.save(user);
		Festival festival = createFestival(user);
		festivalRepository.save(festival);
		Review review = createReview(user, festival);
		reviewRepository.save(review);

		// when
		CompletableFuture<List<Long>> badgeIdsFuture = badgeService.giveReviewBadge(user.getId());
		List<Long> badgeIds = badgeIdsFuture.get();

		// then
		assertEquals(1, badgeIds.size());
		assertTrue(badgeIds.contains(FIRST_REVIEW_BADGE_ID));
	}

	@DisplayName("리뷰 뱃지 수여 - 열정적인 리뷰어")
	@Test
	void giveReviewBadge_PassionateReviewer() throws ExecutionException, InterruptedException {
		// given
		User user = createUser();
		userRepository.save(user);
		Festival festival = createFestival(user);
		festivalRepository.save(festival);
		for (int reviewCount = 0; reviewCount < 5; reviewCount++) {
			Review review = createReview(user, festival);
			reviewRepository.save(review);
		}

		UserBadge userBadge = createUserBadge(user, FIRST_REVIEW_BADGE_ID);
		userBadgeRepository.save(userBadge);

		// when
		CompletableFuture<List<Long>> badgeIdsFuture = badgeService.giveReviewBadge(user.getId());
		List<Long> badgeIds = badgeIdsFuture.get();

		// then
		assertEquals(1, badgeIds.size());
		assertTrue(badgeIds.contains(PASSIONATE_REVIEWER_BADGE_ID));
	}

	@DisplayName("리뷰 뱃지 수여 - 역사 애호가")
	@Test
	void giveReviewBadge_HistoryLover() throws ExecutionException, InterruptedException {
		// given
		User user = createUser();
		userRepository.save(user);

		Festival festival = createFestival(user);
		festivalRepository.save(festival);

		FestivalCategory festivalCategory = createFestivalCategory(festival, CategoryConstants.CATEGORY_HISTORY);
		festivalCategoryRepository.save(festivalCategory);

		for (int reviewCount = 0; reviewCount < 2; reviewCount++) {
			Review review = createReview(user, festival);
			reviewRepository.save(review);
		}

		UserBadge userBadge = createUserBadge(user, FIRST_REVIEW_BADGE_ID);
		userBadgeRepository.save(userBadge);

		// when
		CompletableFuture<List<Long>> badgeIdsFuture = badgeService.giveReviewBadge(user.getId());
		List<Long> badgeIds = badgeIdsFuture.get();

		// then
		assertEquals(1, badgeIds.size());
		assertTrue(badgeIds.contains(HISTORY_LOVER_BADGE_ID));
	}

	@DisplayName("뱃지 수여 - 페스티벌 첫 등록")
	@Test
	void test() throws ExecutionException, InterruptedException {
		// given
		User user = createUser();
		userRepository.save(user);

		Festival festival = createFestival(user);
		festivalRepository.save(festival);

		// when
		CompletableFuture<List<Long>> badgeIdsFuture = badgeService.giveFestivalBadge(user.getId());
		List<Long> badgeIds = badgeIdsFuture.get();

		// then
		assertEquals(1, badgeIds.size());
		assertTrue(badgeIds.contains(FIRST_FESTIVAL_BADGE_ID));
	}

	private UserBadge createUserBadge(User user, Long badgeId) {
		return UserBadge.builder()
			.userId(user.getId())
			.badgeId(badgeId)
			.build();
	}

	private Review createReview(User user, Festival festival) {
		return Review.builder()
			.userId(user.getId())
			.festivalId(festival.getId())
			.rating(35)
			.content("리뷰 내용")
			.build();
	}

	// 카테고리 별 페스티벌
	private Festival createFestival(User user) {
		return Festival.builder()
			.userId(user.getId())
			.name("페스티벌 이름")
			.startDate(LocalDate.of(2021, 8, 1))
			.endDate(LocalDate.of(2021, 8, 1))
			.address("주소")
			.sidoId(1L)
			.sigungu("시군구")
			.latitude(1.0)
			.longitude(1.0)
			.tip("팁")
			.homepageUrl("홈페이지")
			.instagramUrl("인스타그램")
			.fee("요금")
			.description("페스티벌 설명")
			.ticketLink("티켓 링크")
			.playtime("행사 시간")
			.isPending(false)
			.contentId("콘텐츠 ID")
			.build();
	}

	private Category createCategory(Long categoryId) {
		return Category.builder()
			.id(categoryId)
			.name("카테고리 이름")
			.emoji("\uD83D\uDC30")
			.build();
	}

	private FestivalCategory createFestivalCategory(Festival festival, Long categoryId) {
		return FestivalCategory.builder()
			.festivalId(festival.getId())
			.categoryId(categoryId)
			.build();
	}

	private Badge createBadge(Long id, BadgeType type) {
		return Badge.builder()
			.id(id)
			.name("뱃지 이름")
			.description("뱃지 설명")
			.imageUrl("이미지 URL")
			.type(type)
			.build();
	}

	private static User createUser() {
		return User.builder()
			.email("fiesta@odiga.com")
			.nickname("테스트 유저")
			.userTypeId(1L)
			.statusMessage("상태 메시지")
			.profileImage("프로필 이미지")
			.build();
	}
}
