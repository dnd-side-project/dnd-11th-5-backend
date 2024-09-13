package com.odiga.fiesta.user.service;

import static com.odiga.fiesta.common.error.ErrorCode.*;
import static java.util.Objects.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odiga.fiesta.category.domain.Category;
import com.odiga.fiesta.category.repository.CategoryRepository;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.companion.domain.Companion;
import com.odiga.fiesta.companion.repository.CompanionRepository;
import com.odiga.fiesta.festival.dto.response.FestivalInfoWithBookmark;
import com.odiga.fiesta.festival.repository.FestivalRepository;
import com.odiga.fiesta.mood.domain.Mood;
import com.odiga.fiesta.mood.repository.MoodRepository;
import com.odiga.fiesta.priority.domain.Priority;
import com.odiga.fiesta.priority.repository.PriorityRepository;
import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.domain.UserType;
import com.odiga.fiesta.user.domain.mapping.UserCategory;
import com.odiga.fiesta.user.domain.mapping.UserCompanion;
import com.odiga.fiesta.user.domain.mapping.UserMood;
import com.odiga.fiesta.user.domain.mapping.UserPriority;
import com.odiga.fiesta.user.dto.request.ProfileCreateRequest;
import com.odiga.fiesta.user.dto.response.ProfileCreateResponse;
import com.odiga.fiesta.user.repository.UserCategoryRepository;
import com.odiga.fiesta.user.repository.UserCompanionRepository;
import com.odiga.fiesta.user.repository.UserMoodRepository;
import com.odiga.fiesta.user.repository.UserPriorityRepository;
import com.odiga.fiesta.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
	private final FestivalRepository festivalRepository;

	private final UserCategoryRepository userCategoryRepository;
	private final UserCompanionRepository userCompanionRepository;
	private final UserMoodRepository userMoodRepository;
	private final UserPriorityRepository userPriorityRepository;

	private final PriorityRepository priorityRepository;
	private final CompanionRepository companionRepository;
	private final CategoryRepository categoryRepository;
	private final MoodRepository moodRepository;

	private final UserRepository userRepository;
	private final UserTypeService userTypeService;

	// 프로필 생성
	@Transactional
	public ProfileCreateResponse createProfile(User user, ProfileCreateRequest request) {

		checkLogin(user);

		List<Long> priorityIds = request.getPriorityIds();
		List<Long> moodIds = request.getMoodIds();
		List<Long> categoryIds = request.getCategoryIds();
		List<Long> companionIds = request.getCompanionIds();

		validatePriorities(priorityIds);
		validateMoods(moodIds);
		validateCategories(categoryIds);
		validateCompanions(companionIds);

		saveUserPriorities(user.getId(), priorityIds);
		saveUserMoods(user.getId(), moodIds);
		saveUserCategories(user.getId(), categoryIds);
		saveUserCompanions(user.getId(), companionIds);

		UserType userType = userTypeService.getTopNUserTypes(categoryIds, moodIds, 1).getFirst();

		user.updateUserType(userType.getId());
		userRepository.save(user);

		// response
		return ProfileCreateResponse.builder()
			.userTypeId(userType.getId())
			.userTypeName(userType.getName())
			.userTypeImage(userType.getCardImage())
			.build();
	}

	public Page<FestivalInfoWithBookmark> getBookmarkedFestivals(User user, Pageable pageable) {
		checkLogin(user);

		Page<FestivalInfoWithBookmark> festivals = festivalRepository.findBookmarkedFestivals(user.getId(),
			pageable);

		return festivals;
	}

	private void saveUserCompanions(final Long userId, List<Long> companionIds) {
		userCompanionRepository.saveAll(
			companionIds.stream()
				.map(companionId -> UserCompanion.builder()
					.userId(userId)
					.companionId(companionId)
					.build())
				.toList()
		);
	}

	private void saveUserCategories(final Long userId, List<Long> categoryIds) {
		userCategoryRepository.saveAll(
			categoryIds.stream()
				.map(categoryId -> UserCategory.builder()
					.userId(userId)
					.categoryId(categoryId)
					.build())
				.toList()
		);
	}

	private void saveUserMoods(final Long userId, List<Long> moodIds) {
		userMoodRepository.saveAll(
			moodIds.stream()
				.map(moodId -> UserMood.builder()
					.userId(userId)
					.moodId(moodId)
					.build())
				.toList()
		);
	}

	private void saveUserPriorities(final Long userId, List<Long> priorityIds) {
		userPriorityRepository.saveAll(
			priorityIds.stream()
				.map(priorityId -> UserPriority.builder()
					.userId(userId)
					.priorityId(priorityId)
					.build())
				.toList()
		);
	}

	private void validateCompanions(List<Long> companionIds) {
		List<Companion> companions = companionRepository.findAllById(companionIds);
		if (companions.size() != companionIds.size()) {
			throw new CustomException(COMPANION_NOT_FOUND);
		}
	}

	private void validateCategories(List<Long> categoryIds) {
		List<Category> categories = categoryRepository.findAllById(categoryIds);
		if (categories.size() != categoryIds.size()) {
			throw new CustomException(CATEGORY_NOT_FOUND);
		}
	}

	private void validateMoods(List<Long> moodIds) {
		List<Mood> moods = moodRepository.findAllById(moodIds);
		if (moods.size() != moodIds.size()) {
			throw new CustomException(MOOD_NOT_FOUND);
		}
	}

	private void validatePriorities(List<Long> priorityIds) {
		List<Priority> priorities = priorityRepository.findAllById(priorityIds);
		if (priorities.size() != priorityIds.size()) {
			throw new CustomException(PRIORITY_NOT_FOUND);
		}
	}

	private void checkLogin(User user) {
		if (isNull(user) || isNull(user.getId())) {
			throw new CustomException(USER_NOT_FOUND);
		}

		if (!userRepository.existsById(user.getId())) {
			throw new CustomException(USER_NOT_FOUND);
		}
	}
}
