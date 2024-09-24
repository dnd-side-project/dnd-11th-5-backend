package com.odiga.fiesta.badge.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odiga.fiesta.badge.domain.BadgeType;
import com.odiga.fiesta.badge.domain.UserBadge;
import com.odiga.fiesta.badge.repository.BadgeRepository;
import com.odiga.fiesta.badge.repository.UserBadgeRepository;
import com.odiga.fiesta.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BadgeService {

	// 뱃지를 수여해보자.
	private final BadgeRepository badgeRepository;
	private final UserBadgeRepository userBadgeRepository;
	private final UserRepository userRepository;

	private static final Long USER_JOIN_BADGE_ID = 1L;

	// 유저 뱃지 수여
	@Transactional
	@Async
	public CompletableFuture<List<Long>> giveUserBadge(Long userId) {

		// 현재 유저가 어떤 뱃지를 가지고 있는지 확인해야 한다.
		List<Long> givenBadgeIds = new ArrayList<>();
		Map<Long, Boolean> userBadgeMap = new HashMap<>();

		// 타입이 user인 뱃지 id가 뭔지 조회한다.
		List<Long> badgeIds = badgeRepository.findIdsByType(BadgeType.USER);
		log.info("badgeIds: {}", badgeIds);

		for (Long badgeId : badgeIds) {
			userBadgeMap.put(badgeId, false);
		}

		// 현재 유저가 해당 뱃지들을 가지고 있는지 확인한다.
		List<Long> currentUserBadgeIds = userBadgeRepository.findBadgeIdByUserIdAndBadgeIdIn(userId, badgeIds);
		for (Long badgeId : currentUserBadgeIds) {
			userBadgeMap.put(badgeId, true);
		}

		// 조건을 확인하고 뱃지를 수여한다.
		for (Long badgeId : badgeIds) {
			if (isUserNotOwnedBadge(badgeId, userBadgeMap) && isBadgeCondition(userId, badgeId)) {
				userBadgeRepository.save(UserBadge.builder().badgeId(badgeId).userId(userId).build());
				givenBadgeIds.add(badgeId);
			}
		}

		return CompletableFuture.completedFuture(givenBadgeIds);
	}

	private boolean isBadgeCondition(long userId, long badgeId) {

		if (badgeId == USER_JOIN_BADGE_ID) {
			// 회원가입 여부 확인하기
			userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
			return true;
		}

		return false;
	}

	private static boolean isUserNotOwnedBadge(Long badgeId, Map<Long, Boolean> userBadgeMap) {
		return !userBadgeMap.get(badgeId);
	}
}
