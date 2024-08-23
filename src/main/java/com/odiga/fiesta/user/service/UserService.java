package com.odiga.fiesta.user.service;

import static com.odiga.fiesta.common.error.ErrorCode.*;
import static java.util.Objects.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.domain.UserType;
import com.odiga.fiesta.user.domain.mapping.UserCategory;
import com.odiga.fiesta.user.domain.mapping.UserCompanion;
import com.odiga.fiesta.user.domain.mapping.UserMood;
import com.odiga.fiesta.user.domain.mapping.UserPriority;
import com.odiga.fiesta.user.dto.request.UserRequest;
import com.odiga.fiesta.user.dto.response.UserResponse;
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

	private final UserCategoryRepository userCategoryRepository;
	private final UserCompanionRepository userCompanionRepository;
	private final UserMoodRepository userMoodRepository;
	private final UserPriorityRepository userPriorityRepository;
	private final UserRepository userRepository;
	private final UserTypeService userTypeService;

	// 프로필 생성
	@Transactional
	public UserResponse.createProfileDTO createProfile(User user, UserRequest.createProfileDTO request) {

		checkLogin(user);

		// 유저 유형 도출
		List<Long> mood = request.getMood();
		List<Long> category = request.getCategory();

		UserType userType = userTypeService.getTopNUserTypes(category, mood, 1).getFirst();

		User savedUser = User.builder()
			.id(user.getId())
			.userTypeId(userType.getId())
			.nickname(user.getNickname())
			.statusMessage(user.getStatusMessage())
			.profileImage(userType.getProfileImage())
			.email(user.getEmail())
			.build();

		// 회원 정보 업데이트
		userRepository.save(savedUser);

		// 온보딩 정보 저장
		saveOnBoardingInfo(user.getId(), request);

		// DTO 반환
		return UserResponse.createProfileDTO.builder()
			.userTypeId(userType.getId())
			.userTypeName(userType.getName())
			.userTypeImage(userType.getCardImage())
			.build();
	}

	// 온보딩 정보 저장
	private void saveOnBoardingInfo(Long userId, UserRequest.createProfileDTO request) {
		List<Long> categories = request.getCategory();
		List<Long> moods = request.getMood();
		List<Long> companions = request.getCompanion();
		List<Long> priorities = request.getPriority();

		// 카테고리 정보 저장
		List<UserCategory> userCategories = categories.stream()
			.map(categoryId -> UserCategory.of(userId, categoryId))
			.collect(Collectors.toList());

		userCategoryRepository.saveAll(userCategories);

		// 분위기 정보 저장
		List<UserMood> userMoods = moods.stream()
			.map(moodId -> UserMood.of(userId, moodId))
			.collect(Collectors.toList());

		userMoodRepository.saveAll(userMoods);

		// 동행유형 정보 저장
		List<UserCompanion> userCompanions = companions.stream()
			.map(companionId -> UserCompanion.of(userId, companionId))
			.collect(Collectors.toList());

		userCompanionRepository.saveAll(userCompanions);

		// 우선순위 정보 저장
		List<UserPriority> userPriorities = priorities.stream()
			.map(priorityId -> UserPriority.of(userId, priorityId))
			.collect(Collectors.toList());

		userPriorityRepository.saveAll(userPriorities);
	}

	private void checkLogin(User user) {
		if (isNull(user)) {
			throw new CustomException(USER_NOT_FOUND);
		}

		userRepository.findById(user.getId())
			.orElseThrow(() -> new CustomException(USER_NOT_FOUND));
	}
}
