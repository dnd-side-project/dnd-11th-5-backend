package com.odiga.fiesta.badge.service;

import static com.odiga.fiesta.badge.domain.BadgeConstants.*;
import static com.odiga.fiesta.category.domain.CategoryConstants.*;
import static com.odiga.fiesta.common.error.ErrorCode.*;
import static java.util.Objects.*;

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
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.festival.repository.FestivalRepository;
import com.odiga.fiesta.review.repository.ReviewRepository;
import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.dto.response.UserBadgeResponse;
import com.odiga.fiesta.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BadgeService {

	public static final int PASSIONATE_REVIEWER_THRESHOLD = 5;
	public static final int REVIEW_BADGE_COUNT_THRESHOLD = 2;

	private final BadgeRepository badgeRepository;
	private final UserBadgeRepository userBadgeRepository;
	private final UserRepository userRepository;
	private final ReviewRepository reviewRepository;
	private final FestivalRepository festivalRepository;

	// 현재 유저의 뱃지 조회
	public List<UserBadgeResponse> getUserBadges(User user) {
		validateUser(user);
		return badgeRepository.findUserBadges(user.getId());
	}

	@Async
	@Transactional
	public CompletableFuture<List<Long>> giveFestivalBadge(Long userId) {
		return CompletableFuture.completedFuture(giveBadge(userId, BadgeType.FESTIVAL));
	}

	@Async
	@Transactional
	public CompletableFuture<List<Long>> giveReviewBadge(Long userId) {
		return CompletableFuture.completedFuture(giveBadge(userId, BadgeType.REVIEW));
	}

	@Async
	@Transactional
	public CompletableFuture<List<Long>> giveUserBadge(Long userId) {
		return CompletableFuture.completedFuture(giveBadge(userId, BadgeType.USER));
	}

	private List<Long> giveBadge(Long userId, BadgeType badgeType) {
		List<Long> givenBadgeIds = new ArrayList<>();
		Map<Long, Boolean> userBadgeMap = new HashMap<>();

		// 특정 뱃지 타입에 대한 id를 조회
		List<Long> badgeIds = badgeRepository.findIdsByType(badgeType);
		for (Long badgeId : badgeIds) {
			userBadgeMap.put(badgeId, false);
		}

		// 현재 유저가 해당 뱃지를 가지고 있는지 확인
		List<Long> currentUserBadgeIds = userBadgeRepository.findBadgeIdByUserIdAndBadgeIdIn(userId, badgeIds);
		for (Long badgeId : currentUserBadgeIds) {
			userBadgeMap.put(badgeId, true);
		}

		// 조건을 확인하고 뱃지를 수여
		for (Long badgeId : badgeIds) {
			if (isUserNotOwnedBadge(badgeId, userBadgeMap) && isBadgeCondition(userId, badgeId)) {
				userBadgeRepository.save(UserBadge.builder().badgeId(badgeId).userId(userId).build());
				givenBadgeIds.add(badgeId);
			}
		}

		return givenBadgeIds;
	}

	private boolean isBadgeCondition(long userId, long badgeId) {

		if (badgeId == USER_JOIN_BADGE_ID) {
			// TODO: soft delete 전환 시, 탈퇴한 회원인지 검증 필요
			userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
			return true;
		}

		if (badgeId == FIRST_REVIEW_BADGE_ID) {
			return reviewRepository.existsByUserId(userId);
		}

		if (badgeId == PASSIONATE_REVIEWER_BADGE_ID) {
			return reviewRepository.countByUserId(userId) >= PASSIONATE_REVIEWER_THRESHOLD;
		}

		if (badgeId == HISTORY_LOVER_BADGE_ID) {
			return reviewRepository.countByUserIdAndCategoryId(userId, CATEGORY_HISTORY)
				>= REVIEW_BADGE_COUNT_THRESHOLD;
		}

		if (badgeId == MUSIC_LOVER_BADGE_ID) {
			return reviewRepository.countByUserIdAndCategoryId(userId, CATEGORY_MUSIC_AND_DANCE)
				>= REVIEW_BADGE_COUNT_THRESHOLD;
		}

		if (badgeId == ACTIVITY_BADGE_ID) {
			return reviewRepository.countByUserIdAndCategoryId(userId, CATEGORY_ACTIVITY)
				>= REVIEW_BADGE_COUNT_THRESHOLD;
		}

		if (badgeId == FOODIE_LOVER_BADGE_ID) {
			return reviewRepository.countByUserIdAndCategoryId(userId, CATEGORY_FOOD_AND_DRINK)
				>= REVIEW_BADGE_COUNT_THRESHOLD;
		}

		if (badgeId == MOVIE_LOVER_BADGE_ID) {
			return reviewRepository.countByUserIdAndCategoryId(userId, CATEGORY_MOVIE) >= REVIEW_BADGE_COUNT_THRESHOLD;
		}

		if (badgeId == FIREWORKS_LOVER_BADGE_ID) {
			return reviewRepository.countByUserIdAndCategoryId(userId, CATEGORY_FIREWORKS)
				>= REVIEW_BADGE_COUNT_THRESHOLD;
		}

		if (badgeId == NATURE_LOVER_BADGE_ID) {
			return reviewRepository.countByUserIdAndCategoryId(userId, CATEGORY_NATURE) >= REVIEW_BADGE_COUNT_THRESHOLD;
		}

		if (badgeId == NIGHT_LOVER_BADGE_ID) {
			return reviewRepository.countByUserIdAndCategoryId(userId, CATEGORY_NIGHT) >= REVIEW_BADGE_COUNT_THRESHOLD;
		}

		if (badgeId == ART_LOVER_BADGE_ID) {
			return reviewRepository.countByUserIdAndCategoryId(userId, CATEGORY_ART) >= REVIEW_BADGE_COUNT_THRESHOLD;
		}

		if (badgeId == CULTURE_LOVER_BADGE_ID) {
			return reviewRepository.countByUserIdAndCategoryId(userId, CATEGORY_CULTURE)
				>= REVIEW_BADGE_COUNT_THRESHOLD;
		}

		if (badgeId == UNIQUE_LOVER_BADGE_ID) {
			return reviewRepository.countByUserIdAndCategoryId(userId, CATEGORY_UNIQUE_FESTIVAL)
				>= REVIEW_BADGE_COUNT_THRESHOLD;
		}

		if (badgeId == FIRST_FESTIVAL_BADGE_ID) {
			return festivalRepository.existsByUserId(userId);
		}

		return false;
	}

	private static boolean isUserNotOwnedBadge(Long badgeId, Map<Long, Boolean> userBadgeMap) {
		return !userBadgeMap.get(badgeId);
	}

	private void validateUser(User user) {
		if (isNull(user) || isNull(user.getId())) {
			throw new CustomException(USER_NOT_FOUND);
		}

		if (!userRepository.existsById(user.getId())) {
			throw new CustomException(USER_NOT_FOUND);
		}
	}
}
