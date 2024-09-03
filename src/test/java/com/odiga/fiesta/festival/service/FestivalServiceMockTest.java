package com.odiga.fiesta.festival.service;

import static com.odiga.fiesta.common.error.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.data.redis.core.ZSetOperations.*;
import static org.springframework.http.MediaType.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.odiga.fiesta.MockTestSupport;
import com.odiga.fiesta.common.PageResponse;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.common.util.FileUtils;
import com.odiga.fiesta.common.util.RedisUtils;
import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.domain.FestivalCategory;
import com.odiga.fiesta.festival.domain.FestivalMood;
import com.odiga.fiesta.festival.dto.request.FestivalCreateRequest;
import com.odiga.fiesta.festival.dto.response.FestivalBasic;
import com.odiga.fiesta.festival.repository.FestivalCategoryRepository;
import com.odiga.fiesta.festival.repository.FestivalImageRepository;
import com.odiga.fiesta.festival.repository.FestivalModificationRequestRepository;
import com.odiga.fiesta.festival.repository.FestivalMoodRepository;
import com.odiga.fiesta.festival.repository.FestivalRepository;
import com.odiga.fiesta.festival.repository.FestivalUserTypeRepository;
import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.domain.UserType;
import com.odiga.fiesta.user.repository.UserRepository;
import com.odiga.fiesta.user.service.UserTypeService;

class FestivalServiceMockTest extends MockTestSupport {

	@Mock
	private RedisUtils redisUtils;

	@Mock
	private FestivalRepository festivalRepository;

	@Mock
	private UserTypeService userTypeService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private FestivalUserTypeRepository festivalUserTypeRepository;

	@Mock
	private FestivalImageRepository festivalImageRepository;

	@Mock
	private FestivalCategoryRepository festivalCategoryRepository;

	@Mock
	private FestivalMoodRepository festivalMoodRepository;

	@Mock
	private FestivalModificationRequestRepository festivalModificationRequestRepository;

	@Mock
	private FileUtils fileUtils;

	@InjectMocks
	private FestivalService festivalService;

	private static final String RANKING_KEY = "testKey";
	private static final Double SCORE_INCREMENT_AMOUNT = 1.0;
	private FestivalBasic searchItem;
	private String itemIdToString;

	@BeforeEach
	public void setUp() {
		searchItem = new FestivalBasic(1L, "락페");
		itemIdToString = searchItem.getFestivalId().toString();
	}

	@DisplayName("페스티벌 실시간 랭킹 집계 - 페스티벌이 처음 검색되는 경우")
	@Test
	public void testUpdateSearchRanking_NewScore() {
		// given // when
		when(redisUtils.zScore(RANKING_KEY, itemIdToString)).thenReturn(null);
		festivalService.updateSearchRanking(RANKING_KEY, searchItem);
		// then
		verify(redisUtils, times(1)).zAdd(eq(RANKING_KEY), eq(itemIdToString), eq(SCORE_INCREMENT_AMOUNT));
	}

	@DisplayName("페스티벌 실시간 랭킹 집계 - 페스티벌의 점수가 누적되는 경우")
	@Test
	public void testUpdateSearchRanking_IncrementScore() {
		// given
		final Double initialScore = 2.0;

		// when
		when(redisUtils.zScore(RANKING_KEY, itemIdToString)).thenReturn(initialScore);
		festivalService.updateSearchRanking(RANKING_KEY, searchItem);

		// then
		verify(redisUtils, times(1)).zAdd(eq(RANKING_KEY), eq(itemIdToString),
			eq(initialScore + SCORE_INCREMENT_AMOUNT));
	}

	@DisplayName("페스티벌 실시간 랭킹 확인")
	@Test
	void testGetTrendingFestival_WithResults() {
		Long page = 0L;
		Integer size = 10;

		TypedTuple tuple = mock(TypedTuple.class);
		when(tuple.getValue()).thenReturn(itemIdToString);

		Set<TypedTuple> set = new HashSet<>();
		set.add(tuple);

		when(redisUtils.zRevrange(anyString(), anyLong(), anyLong())).thenReturn(set);
		when(redisUtils.zSize(anyString())).thenReturn(1L);
		when(festivalRepository.findAllById(anyIterable())).thenReturn(List.of(
			Festival.builder()
				.id(searchItem.getFestivalId())
				.name(searchItem.getName())
				.build()
		));

		PageResponse<FestivalBasic> response = festivalService.getTrendingFestival(RANKING_KEY, page, size);

		assertEquals(1, response.getContent().size());
		assertEquals(1L, response.getTotalElements());
		assertEquals(1, response.getTotalElements());
		assertEquals(page, response.getPageNumber());
		assertEquals(1, response.getTotalPages());

		FestivalBasic festival = response.getContent().get(0);
		assertEquals(searchItem.getFestivalId(), festival.getFestivalId());
		assertEquals(searchItem.getName(), festival.getName());
	}

	@Nested
	@DisplayName("페스티벌 생성")
	class FestivalCreationTest {
		User user = User.builder()
			.email("fiesta@odiga.com")
			.userTypeId(1L)
			.nickname("피에스타")
			.profileImage("profileImage")
			.statusMessage("상태메시지")
			.build();

		List<MultipartFile> files = List.of(
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

		Festival festival = createFestival();

		UserType userType1 = UserType.builder()
			.id(1L)
			.name("유저타입")
			.build();

		UserType userType2 = UserType.builder()
			.id(2L)
			.name("유저타입")
			.build();

		FestivalCreateRequest request = FestivalCreateRequest.builder()
			.name("페스티벌 이름")
			.description("페스티벌 설명")
			.startDate(LocalDate.of(2024, 10, 4))
			.endDate(LocalDate.of(2024, 10, 4))
			.address("주소")
			.latitude(35.1731)
			.longitude(129.0714)
			.sido("부산")
			.sigungu("해운대구")
			.playtime("플레이타임")
			.homepageUrl("홈페이지")
			.instagramUrl("인스타그램")
			.ticketLink("티켓링크")
			.fee("입장료")
			.categoryIds(List.of(1L, 2L))
			.moodIds(List.of(1L, 2L))
			.tip("팁")
			.build();

		@DisplayName("페스티벌 생성 - 성공")
		@Test
		void createFestival_Success() {
			// given
			given(userRepository.existsById(1L)).willReturn(true);
			given(userTypeService.getTopNUserTypes(List.of(1L, 2L), List.of(1L, 2L), 2))
				.willReturn(List.of(userType1, userType2));
			given(festivalRepository.save(any())).willReturn(festival);

			// when
			festivalService.createFestival(1L, request, files);

			// then
			then(festivalRepository).should().save(any());
		}

		@DisplayName("페스티벌 생성 - 성공 (이미지가 없는 경우)")
		@Test
		void createFestival_SuccessWithoutImages() {
			// given
			given(userRepository.existsById(1L)).willReturn(true);
			given(userTypeService.getTopNUserTypes(List.of(1L, 2L), List.of(1L, 2L), 2))
				.willReturn(List.of(userType1, userType2));
			given(festivalRepository.save(any())).willReturn(festival);

			// when
			festivalService.createFestival(1L, request, null);

			// then
			then(festivalRepository).should().save(any());
		}

		@DisplayName("페스티벌 생성 - 성공 (list 항목에 중복있으면 제거하고 저장)")
		@Test
		void createFestival_RemoveDuplicatedKeywords() {
			// given
			FestivalCreateRequest listDuplicatedRequest = FestivalCreateRequest.builder()
				.name("페스티벌 이름")
				.description("페스티벌 설명")
				.startDate(LocalDate.of(2024, 10, 4))
				.endDate(LocalDate.of(2024, 10, 4))
				.address("주소")
				.latitude(35.1731)
				.longitude(129.0714)
				.sido("부산")
				.sigungu("해운대구")
				.playtime("플레이타임")
				.homepageUrl("홈페이지")
				.instagramUrl("인스타그램")
				.ticketLink("티켓링크")
				.fee("입장료")
				.categoryIds(List.of(1L, 1L, 2L))
				.moodIds(List.of(1L, 2L, 2L))
				.tip("팁")
				.build();

			given(userRepository.existsById(1L)).willReturn(true);
			given(userTypeService.getTopNUserTypes(List.of(1L, 2L), List.of(1L, 2L), 2))
				.willReturn(List.of(userType1, userType2));
			given(festivalRepository.save(any())).willReturn(festival);

			// when
			festivalService.createFestival(1L, listDuplicatedRequest, null);

			// then
			ArgumentCaptor<List<FestivalCategory>> categoryCaptor = ArgumentCaptor.forClass(List.class);
			ArgumentCaptor<List<FestivalMood>> moodCaptor = ArgumentCaptor.forClass(List.class);

			verify(festivalCategoryRepository).saveAll(categoryCaptor.capture());
			verify(festivalMoodRepository).saveAll(moodCaptor.capture());

			List<FestivalCategory> savedCategories = categoryCaptor.getValue();
			List<FestivalMood> savedMoods = moodCaptor.getValue();

			assertEquals(2, savedCategories.size());
			assertEquals(2, savedMoods.size());

			List<Long> expectedCategoryIds = List.of(1L, 2L);
			for (FestivalCategory category : savedCategories) {
				assertTrue(expectedCategoryIds.contains(category.getCategoryId()));
			}

			List<Long> expectedMoodIds = List.of(1L, 2L);
			for (FestivalMood mood : savedMoods) {
				assertTrue(expectedMoodIds.contains(mood.getMoodId()));
			}
		}

		@DisplayName("페스티벌 생성 - 실패, 이미지 갯수 초과")
		@Test
		void createFestival_ImageCountExceeded() {
			// given
			List<MultipartFile> files = List.of(
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

			given(userRepository.existsById(1L)).willReturn(true);

			// when // then
			CustomException exception = assertThrows(CustomException.class, () -> {
				festivalService.createFestival(1L, request, files);
			});

			assertEquals(FESTIVAL_IMAGE_EXCEEDED.getMessage(), exception.getMessage());
		}
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
