package com.odiga.fiesta.badge.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.odiga.fiesta.IntegrationTestSupport;
import com.odiga.fiesta.badge.domain.Badge;
import com.odiga.fiesta.badge.domain.BadgeType;
import com.odiga.fiesta.badge.domain.UserBadge;
import com.odiga.fiesta.badge.repository.BadgeRepository;
import com.odiga.fiesta.badge.repository.UserBadgeRepository;
import com.odiga.fiesta.config.S3MockConfig;
import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.repository.UserRepository;
import com.odiga.fiesta.user.service.UserService;

class BadgeServiceTest extends IntegrationTestSupport {

	@Autowired
	private BadgeService badgeService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BadgeRepository badgeRepository;

	@Autowired
	private UserBadgeRepository userBadgeRepository;

	@BeforeEach
	void setUp() {
		// 기본 뱃지 데이터 셋업
		badgeRepository.save(createBadge(1L, BadgeType.USER));
		badgeRepository.save(createBadge(2L, BadgeType.REVIEW));
		badgeRepository.save(createBadge(3L, BadgeType.FESTIVAL));

		for (long badgeId = 4L; badgeId <= 15L; badgeId++) {
			badgeRepository.save(createBadge(badgeId, BadgeType.REVIEW));
		}
	}

	@AfterEach
	void tearDown() {
		userBadgeRepository.deleteAll();
		badgeRepository.deleteAll();
		userRepository.deleteAll();
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
		assertTrue(badgeIds.contains(1L));
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
