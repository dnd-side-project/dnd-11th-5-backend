package com.odiga.fiesta.user.service;

import static com.odiga.fiesta.common.error.ErrorCode.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.odiga.fiesta.MockTestSupport;
import com.odiga.fiesta.category.domain.Category;
import com.odiga.fiesta.category.repository.CategoryRepository;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.companion.domain.Companion;
import com.odiga.fiesta.companion.repository.CompanionRepository;
import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.dto.response.FestivalInfoWithBookmark;
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

	@DisplayName("유저가 북마크한 페스티벌들을 조회")
	@Test
	void getBookmarkedFestivals() {
		// given
		User currentUser = createNoProfileUser();
		given(userRepository.existsById(currentUser.getId())).willReturn(true);

		Pageable pageable = PageRequest.of(0, 5);
		List<FestivalInfoWithBookmark> festivals = Arrays.asList(
			new FestivalInfoWithBookmark(1L, "Festival 1", "Sido 1", "Sigungu 1", "image1.jpg",
				LocalDate.of(2024, 10, 1), LocalDate.of(2024, 10, 10), true),
			new FestivalInfoWithBookmark(2L, "Festival 2", "Sido 2", "Sigungu 2", "image2.jpg",
				LocalDate.of(2024, 10, 2), LocalDate.of(2024, 10, 11), true)
		);

		Page<FestivalInfoWithBookmark> festivalPage = new PageImpl<>(festivals, pageable, festivals.size());
		given(festivalRepository.findBookmarkedFestivals(currentUser.getId(), pageable)).willReturn(festivalPage);

		// when
		Page<FestivalInfoWithBookmark> result = userService.getBookmarkedFestivals(currentUser, pageable);

		// then
		assertThat(result).isNotNull();  // 반환된 값이 null이 아닌지 확인
		assertThat(result.getTotalElements()).isEqualTo(2);  // 총 북마크된 페스티벌 수 확인
		assertThat(result.getContent())
			.usingRecursiveComparison()
			.isEqualTo(festivals);
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
