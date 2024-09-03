package com.odiga.fiesta.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.odiga.fiesta.MockTestSupport;
import com.odiga.fiesta.category.domain.Category;
import com.odiga.fiesta.category.repository.CategoryRepository;
import com.odiga.fiesta.companion.domain.Companion;
import com.odiga.fiesta.companion.repository.CompanionRepository;
import com.odiga.fiesta.mood.domain.Mood;
import com.odiga.fiesta.mood.repository.MoodRepository;
import com.odiga.fiesta.priority.domain.Priority;
import com.odiga.fiesta.priority.repository.PriorityRepository;
import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.domain.UserType;
import com.odiga.fiesta.user.dto.request.ProfileCreateRequest;
import com.odiga.fiesta.user.dto.response.ProfileCreateResponse;
import com.odiga.fiesta.user.repository.UserCategoryRepository;
import com.odiga.fiesta.user.repository.UserCompanionRepository;
import com.odiga.fiesta.user.repository.UserMoodRepository;
import com.odiga.fiesta.user.repository.UserPriorityRepository;
import com.odiga.fiesta.user.repository.UserRepository;

class UserServiceTest extends MockTestSupport {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PriorityRepository priorityRepository;

	@Mock
	private MoodRepository moodRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private CompanionRepository companionRepository;

	@Mock
	private UserTypeService userTypeService;

	@Mock
	private UserPriorityRepository userPriorityRepository;

	@Mock
	private UserMoodRepository userMoodRepository;

	@Mock
	private UserCategoryRepository userCategoryRepository;

	@Mock
	private UserCompanionRepository userCompanionRepository;

	@DisplayName("프로필 생성 - 성공")
	@Test
	void createProfile_Success() {
		// given
		User user = createNoProfileUser();

		ProfileCreateRequest request = ProfileCreateRequest.builder()
			.priorityIds(List.of(1L, 2L))
			.moodIds(List.of(1L, 2L))
			.categoryIds(List.of(1L, 2L))
			.companionIds(List.of(1L, 2L))
			.build();

		UserType userType = UserType.builder()
			.id(1L)
			.name("힐링러")
			.profileImage("힐링러 프로필 이미지")
			.cardImage("힐링러 카드 이미지")
			.build();

		given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
		given(priorityRepository.findAllById(request.getPriorityIds())).willReturn(
			List.of(Priority.builder().id(1L).build(), Priority.builder().id(2L).build()));
		given(moodRepository.findAllById(request.getMoodIds())).willReturn(
			List.of(Mood.builder().id(1L).build(), Mood.builder().id(2L).build()));
		given(categoryRepository.findAllById(request.getCategoryIds())).willReturn(
			List.of(Category.builder().id(1L).build(), Category.builder().id(2L).build()));
		given(companionRepository.findAllById(request.getCompanionIds())).willReturn(
			List.of(Companion.builder().id(1L).build(), Companion.builder().id(2L).build()));

		given(userTypeService.getTopNUserTypes(request.getCategoryIds(), request.getMoodIds(), 1))
			.willReturn(List.of(userType));

		// when
		ProfileCreateResponse response = userService.createProfile(user, request);

		// then
		assertEquals(userType.getId(), response.getUserTypeId());
		assertEquals(userType.getName(), response.getUserTypeName());
		assertEquals(userType.getCardImage(), response.getUserTypeImage());

		verify(userRepository).save(user);
		verify(userTypeService).getTopNUserTypes(request.getCategoryIds(), request.getMoodIds(), 1);
		verify(userPriorityRepository).saveAll(anyList());
		verify(userMoodRepository).saveAll(anyList());
		verify(userCategoryRepository).saveAll(anyList());
		verify(userCompanionRepository).saveAll(anyList());
	}

	User createNoProfileUser() {
		return User.builder()
			.id(1L)
			.email("fiesta@odiga.com")
			.nickname("피에스타")
			.statusMessage("상태 메시지")
			.profileImage("기본 프로필 이미지")
			.build();
	}
}
