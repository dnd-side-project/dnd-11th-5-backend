package com.odiga.fiesta.user.service;

import static com.odiga.fiesta.common.error.ErrorCode.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.odiga.fiesta.MockTestSupport;
import com.odiga.fiesta.category.domain.Category;
import com.odiga.fiesta.category.repository.CategoryRepository;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.companion.domain.Companion;
import com.odiga.fiesta.companion.repository.CompanionRepository;
import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.repository.FestivalRepository;
import com.odiga.fiesta.mood.domain.Mood;
import com.odiga.fiesta.mood.repository.MoodRepository;
import com.odiga.fiesta.priority.domain.Priority;
import com.odiga.fiesta.priority.repository.PriorityRepository;
import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.domain.UserType;
import com.odiga.fiesta.user.dto.request.ProfileCreateRequest;
import com.odiga.fiesta.user.dto.request.UserInfoUpdateRequest;
import com.odiga.fiesta.user.dto.response.ProfileCreateResponse;
import com.odiga.fiesta.user.dto.response.UserIdResponse;
import com.odiga.fiesta.user.dto.response.UserOnboardingResponse;
import com.odiga.fiesta.user.repository.UserCategoryRepository;
import com.odiga.fiesta.user.repository.UserCompanionRepository;
import com.odiga.fiesta.user.repository.UserMoodRepository;
import com.odiga.fiesta.user.repository.UserPriorityRepository;
import com.odiga.fiesta.user.repository.UserRepository;

class UserServiceMockTest extends MockTestSupport {

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

	@Mock
	private FestivalRepository festivalRepository;

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

		given(userRepository.existsById(user.getId())).willReturn(true);
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

	@DisplayName("프로필 수정 - 이전의 온보딩 정보가 삭제되어야 한다.")
	@Test
	void updateUserInfo_ShouldDeleteOnboardingInfo() {
		// given
		User user = createNoProfileUser();
		Long userId = user.getId();
		given(userRepository.existsById(userId)).willReturn(true);

		ProfileCreateRequest currentOnboardingInfo = createProfileCreateRequest();
		mockRepositoriesForCreateProfileRequest(currentOnboardingInfo);

		UserType nextUserType = UserType.builder()
			.id(2L)
			.name("수정된 user type")
			.profileImage("프로필 이미지")
			.cardImage("카드 이미지")
			.build();

		given(userTypeService.getTopNUserTypes(currentOnboardingInfo.getCategoryIds(),
			currentOnboardingInfo.getMoodIds(), 1))
			.willReturn(List.of(nextUserType));

		// when
		ProfileCreateResponse response = userService.updateProfile(user, currentOnboardingInfo);

		// then
		verifyRepositoriesDeletion(userId);
		verifyRepositoriesSave();
		assertEquals(user.getUserTypeId(), nextUserType.getId());
	}

	@DisplayName("유저 정보 수정 - 성공")
	@Test
	void updateUserInfo_Success() {
		// given
		User user = createNoProfileUser();
		given(userRepository.existsById(user.getId())).willReturn(true);

		UserInfoUpdateRequest request = UserInfoUpdateRequest
			.builder()
			.nickname("수정된 닉네임")
			.statusMessage("수정된 상태 메시지")
			.build();

		// when
		UserIdResponse response = userService.updateUserInfo(user, request);

		// then
		assertThat(response.getUserId()).isEqualTo(user.getId());
		assertThat(user.getNickname()).isEqualTo(request.getNickname());
		assertThat(user.getStatusMessage()).isEqualTo(request.getStatusMessage());
	}

	@DisplayName("유저 정보 수정 - 실패: 닉네임 길이 제한 초과")
	@Test
	void updateUserInfo_ExceedNickNameLength() {
		// given
		User user = createNoProfileUser();
		given(userRepository.existsById(user.getId())).willReturn(true);

		UserInfoUpdateRequest request = UserInfoUpdateRequest
			.builder()
			.nickname("12345678901") // 11자
			.statusMessage("수정된 상태 메시지")
			.build();
		// when
		CustomException exception = assertThrows(CustomException.class,
			() -> userService.updateUserInfo(user, request));

		// then
		assertThat(exception.getErrorCode()).isEqualTo(INVALID_NICKNAME_LENGTH);
	}

	@DisplayName("유저 정보 수정 - 실패: 상테 메시지 길이 제한 초과")
	@Test
	void updateUserInfo_ExceedStatusMessageLength() {
		// given
		User user = createNoProfileUser();
		given(userRepository.existsById(user.getId())).willReturn(true);

		UserInfoUpdateRequest request = UserInfoUpdateRequest
			.builder()
			.nickname("수정된 닉네임")
			.statusMessage("1234567890123456789012345678901") // 31자
			.build();

		// when
		CustomException exception = assertThrows(CustomException.class,
			() -> userService.updateUserInfo(user, request));

		// then
		assertThat(exception.getErrorCode()).isEqualTo(INVALID_STATUS_MESSAGE_LENGTH);
	}

	@DisplayName("유저의 온보딩 정보 조회 - 성공")
	@Test
	void getOnboardingInfo() {
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

		given(userRepository.existsById(user.getId())).willReturn(true);
		given(userCategoryRepository.findCategoryIdsByUserId(user.getId())).willReturn(request.getCategoryIds());
		given(userMoodRepository.findMoodIdsByUserId(user.getId())).willReturn(request.getMoodIds());
		given(userCompanionRepository.findCompanionIdsByUserId(user.getId())).willReturn(request.getCompanionIds());
		given(userPriorityRepository.findPriorityIdsByUserId(user.getId())).willReturn(request.getPriorityIds());

		// when
		UserOnboardingResponse onboardingInfo = userService.getOnboardingInfo(user);

		// then
		assertThat(onboardingInfo)
			.extracting("categoryIds", "moodIds", "companionIds", "priorityIds")
			.containsExactly(
				request.getCategoryIds(),
				request.getMoodIds(),
				request.getCompanionIds(),
				request.getPriorityIds()
			);
	}

	private static ProfileCreateRequest createProfileCreateRequest() {
		return ProfileCreateRequest.builder()
			.categoryIds(List.of(11L, 12L))
			.moodIds(List.of(11L, 12L, 13L))
			.companionIds(List.of(11L, 12L))
			.priorityIds(List.of(11L, 12L, 13L))
			.build();
	}

	private void mockRepositoriesForCreateProfileRequest(ProfileCreateRequest request) {
		given(priorityRepository.findAllById(request.getPriorityIds()))
			.willReturn(List.of(
				Priority.builder().id(11L).build(),
				Priority.builder().id(12L).build(),
				Priority.builder().id(13L).build()
			));

		given(moodRepository.findAllById(request.getMoodIds()))
			.willReturn(List.of(
				Mood.builder().id(11L).build(),
				Mood.builder().id(12L).build(),
				Mood.builder().id(13L).build()
			));

		given(categoryRepository.findAllById(request.getCategoryIds()))
			.willReturn(List.of(
				Category.builder().id(11L).build(),
				Category.builder().id(12L).build()
			));

		given(companionRepository.findAllById(request.getCompanionIds()))
			.willReturn(List.of(
				Companion.builder().id(11L).build(),
				Companion.builder().id(12L).build()
			));
	}

	private void verifyRepositoriesDeletion(Long userId) {
		// 카테고리 정보 삭제 확인
		verify(userCategoryRepository).deleteByUserId(userId);
		verify(userCompanionRepository).deleteByUserId(userId);
		verify(userMoodRepository).deleteByUserId(userId);
		verify(userPriorityRepository).deleteByUserId(userId);
	}

	private void verifyRepositoriesSave() {
		// 새로운 정보 저장 여부 확인
		verify(userCategoryRepository).saveAll(anyList());
		verify(userCompanionRepository).saveAll(anyList());
		verify(userMoodRepository).saveAll(anyList());
		verify(userPriorityRepository).saveAll(anyList());
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

	private static Festival createFestival(LocalDate startDate, LocalDate endDate) {
		return Festival.builder()
			.userId(1L)
			.name("페스티벌 이름")
			.description("페스티벌 설명")
			.startDate(startDate)
			.endDate(endDate)
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
