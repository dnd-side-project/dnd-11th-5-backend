package com.odiga.fiesta.festival.service;

import static com.odiga.fiesta.common.error.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;

import com.odiga.fiesta.IntegrationTestSupport;
import com.odiga.fiesta.category.domain.Category;
import com.odiga.fiesta.category.repository.CategoryRepository;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.domain.FestivalBookmark;
import com.odiga.fiesta.festival.domain.FestivalCategory;
import com.odiga.fiesta.festival.domain.FestivalImage;
import com.odiga.fiesta.festival.domain.FestivalMood;
import com.odiga.fiesta.festival.dto.request.FestivalFilterRequest;
import com.odiga.fiesta.festival.dto.response.CategoryResponse;
import com.odiga.fiesta.festival.dto.response.DailyFestivalContents;
import com.odiga.fiesta.festival.dto.response.FestivalAndLocation;
import com.odiga.fiesta.festival.dto.response.FestivalDetailResponse;
import com.odiga.fiesta.festival.dto.response.FestivalImageResponse;
import com.odiga.fiesta.festival.dto.response.FestivalInfo;
import com.odiga.fiesta.festival.dto.response.FestivalInfoWithBookmark;
import com.odiga.fiesta.festival.dto.response.FestivalMonthlyResponse;
import com.odiga.fiesta.festival.dto.response.MoodResponse;
import com.odiga.fiesta.festival.repository.FestivalBookmarkRepository;
import com.odiga.fiesta.festival.repository.FestivalCategoryRepository;
import com.odiga.fiesta.festival.repository.FestivalImageRepository;
import com.odiga.fiesta.festival.repository.FestivalMoodRepository;
import com.odiga.fiesta.festival.repository.FestivalRepository;
import com.odiga.fiesta.mood.domain.Mood;
import com.odiga.fiesta.mood.repository.MoodRepository;
import com.odiga.fiesta.sido.domain.Sido;
import com.odiga.fiesta.sido.repository.SidoRepository;
import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class FestivalServiceTest extends IntegrationTestSupport {

	private static final Clock CURRENT_CLOCK = Clock.fixed(Instant.parse("2024-01-01T10:00:00Z"), ZoneOffset.UTC);
	private static final LocalDate today = LocalDate.of(2024, 1, 1);

	@Autowired
	private FestivalService festivalService;

	@Autowired
	private FestivalRepository festivalRepository;

	@Autowired
	private FestivalImageRepository festivalImageRepository;

	@Autowired
	private FestivalCategoryRepository festivalCategoryRepository;

	@Autowired
	private FestivalMoodRepository festivalMoodRepository;

	@Autowired
	private SidoRepository sidoRepository;

	@Autowired
	private FestivalBookmarkRepository festivalBookmarkRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private MoodRepository moodRepository;

	@Autowired
	private UserRepository userRepository;

	@SpyBean
	private Clock clock;

	@BeforeEach
	void beforeEach() {
		doReturn(Instant.now(CURRENT_CLOCK))
			.when(clock)
			.instant();
	}

	@DisplayName("페스티벌 월간 조회 - startDate 와 endDate 사이에 해당 월이 끼어있어도 페스티벌이 포함되어야 한다.")
	@Test
	void getMonthlyFestivals() {
		// given
		int year = 2024;
		int month = 4;

		Festival festival1 = createFestival(
			LocalDate.of(2024, 3, 25),
			LocalDate.of(2024, 5, 5));

		festivalRepository.save(festival1);

		// when
		FestivalMonthlyResponse response = festivalService.getMonthlyFestivals(year, month);

		// then
		assertNotNull(response);
		assertEquals(year, response.getYear());
		assertEquals(month, response.getMonth());

		List<DailyFestivalContents> contents = response.getContents();
		YearMonth yearMonth = YearMonth.of(year, month);

		assertEquals(yearMonth.lengthOfMonth(), contents.size());

		List<LocalDate> allDatesInApril = Stream.iterate(yearMonth.atDay(1), date -> date.plusDays(1))
			.limit(yearMonth.lengthOfMonth())
			.toList();

		for (int i = 0; i < contents.size(); i++) {
			DailyFestivalContents dailyContent = contents.get(i);
			assertEquals(allDatesInApril.get(i), dailyContent.getDate());
			assertEquals(1, dailyContent.getFestivals().size());
			assertEquals(festival1.getName(), dailyContent.getFestivals().get(0).getName());
			assertEquals(1, dailyContent.getTotalElements());
		}
	}

	@DisplayName("페스티벌 월간 조회 - 데이터가 3개 이상일 경우, 3개의 데이터만 보인다.")
	@Test
	void getMonthlyFestivals_Limit() {
		// given
		int year = 2024;
		int month = 10;

		List<Festival> festivals = new ArrayList<>();

		for (int i = 0; i < 100; i++) {
			festivals.add(createFestival(
				LocalDate.of(2024, 10, 4),
				LocalDate.of(2024, 10, 4)));
		}

		festivalRepository.saveAll(festivals);

		// when
		FestivalMonthlyResponse response = festivalService.getMonthlyFestivals(year, month);

		// then
		List<DailyFestivalContents> contents = response.getContents();
		assertThat(contents.get(3).getFestivals()).hasSize(3);
		assertThat(contents.get(3).getTotalElements()).isEqualTo(100);
	}

	@DisplayName("데이터가 없어도 빈 리스트가 출력되어야 한다.")
	@Test
	void getMonthlyFestivals_empty() {
		// given
		int year = 2024;
		int month = 1;

		Festival festival1 = createFestival(
			LocalDate.of(2024, 3, 25),
			LocalDate.of(2024, 5, 5));

		festivalRepository.save(festival1);

		// when
		FestivalMonthlyResponse response = festivalService.getMonthlyFestivals(year, month);

		// then
		List<DailyFestivalContents> contents = response.getContents();

		for (int i = 0; i < 31; i++) {
			assertThat(contents.get(i).getFestivals()).isNotNull();
			assertThat(contents.get(i).getFestivals()).isEmpty();
		}
	}

	@DisplayName("페스티벌 일간 조회 - 성공 케이스")
	@Test
	void getFestivalsByDay() {
		// given
		Sido sido = createSido();
		Sido savedSido = sidoRepository.save(sido);

		Festival festival1 = createFestival(LocalDate.of(2024, 10, 1), LocalDate.of(2024, 10, 10), savedSido.getId());
		Festival festival2 = createFestival(LocalDate.of(2024, 10, 5), LocalDate.of(2024, 10, 10), savedSido.getId());

		Festival savedFestival1 = festivalRepository.save(festival1);
		Festival savedFestival2 = festivalRepository.save(festival2);

		FestivalImage image1 = FestivalImage.builder().festivalId(savedFestival1.getId()).imageUrl("imageUrl1").build();
		FestivalImage image2 = FestivalImage.builder().festivalId(savedFestival2.getId()).imageUrl("imageUrl2").build();

		FestivalImage savedImage1 = festivalImageRepository.save(image1);
		FestivalImage savedImage2 = festivalImageRepository.save(image2);

		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<FestivalInfoWithBookmark> result = festivalService.getFestivalsByDay(null, 2024, 10, 4, pageable);

		// then
		assertThat(result.getContent()).hasSize(1)
			.extracting("festivalId", "name", "sido", "sigungu", "thumbnailImage",
				"startDate", "endDate", "isBookmarked")
			.containsExactlyInAnyOrder(
				tuple(savedFestival1.getId(), savedFestival1.getName(), savedSido.getName(),
					savedFestival1.getSigungu(),
					savedImage1.getImageUrl(), savedFestival1.getStartDate(), savedFestival1.getEndDate(), false)
			);
	}

	@DisplayName("페스티벌 필터 조회 - 월 필터 적용")
	@Test
	void getFestivalByFiltersAndSort_startDateFilter() {
		// given
		FestivalFilterRequest filterRequest = FestivalFilterRequest.builder()
			.months(List.of(1, 2, 11, 12))
			.build();

		Pageable pageable = PageRequest.of(0, 6);

		Festival festival1 = createFestival(LocalDate.of(2023, 10, 31), LocalDate.of(2024, 1, 20));
		Festival festival2 = createFestival(LocalDate.of(2024, 2, 10), LocalDate.of(2024, 3, 16));
		Festival festival3 = createFestival(LocalDate.of(2024, 10, 4), LocalDate.of(2025, 3, 10));
		Festival festival4 = createFestival(LocalDate.of(2024, 9, 9), LocalDate.of(2024, 10, 4));

		festivalRepository.saveAll(List.of(festival1, festival2, festival3, festival4));

		// when
		Page<FestivalInfoWithBookmark> responses = festivalService.getFestivalByFiltersAndSort(null,
			filterRequest, null, null, pageable);

		System.out.println("response: " + responses.getContent());
		// then
		assertEquals(3, responses.getTotalElements());
	}

	@DisplayName("페스티벌 필터 조회 - 거리순 정렬 ")
	@Test
	void getFestivalByFiltersAndSort_orderByDist() {
		// given
		FestivalFilterRequest filterRequest = FestivalFilterRequest.builder().build();
		LocalDate currentDate = LocalDate.of(2024, 10, 4);
		Pageable pageable = PageRequest.of(0, 6, Sort.by("dist"));

		Double currentLatitude = 35.1731;
		Double currentLongitude = 129.0714;

		Festival festival1 = createFestival("1", currentDate, currentLatitude, currentLongitude);
		Festival festival2 = createFestival("2", currentDate, currentLatitude, currentLongitude + 20);
		Festival festival3 = createFestival("3", currentDate, currentLatitude, currentLongitude + 50);
		Festival festival4 = createFestival("4", currentDate, currentLatitude, currentLongitude + 10);

		festivalRepository.saveAll(List.of(festival1, festival2, festival3, festival4));

		// when
		Page<FestivalInfoWithBookmark> responses = festivalService.getFestivalByFiltersAndSort(null,
			filterRequest, currentLatitude, currentLongitude, pageable);

		// then
		assertEquals(4, responses.getTotalElements());

		assertThat(responses.getContent())
			.hasSize(4)
			.extracting("name")
			.containsExactly(
				"1", "4", "2", "3"
			);
	}

	@DisplayName("페스티벌 이름 검색")
	@Test
	void getFestivalsByQuery() {
		// given
		Festival festival1 = createFestival("펜타포트");
		Festival festival2 = createFestival("펜타타");
		Festival festival3 = createFestival("락페스티벌");
		festivalRepository.saveAll(List.of(festival1, festival2, festival3));

		Pageable pageable = PageRequest.of(0, 6);

		// when
		Page<FestivalInfoWithBookmark> festivals = festivalService.getFestivalsByQuery(null, "펜타", pageable);

		// then
		assertThat(festivals.getContent())
			.hasSize(2)
			.extracting("name")
			.containsExactly("펜타포트", "펜타타");
	}

	@DisplayName("페스티벌 이름 검색 - 매칭되는 데이터가 없을 떄 빈 리스트 반환")
	@Test
	void getFestivalsByQuery_EmptyResult() {
		// given
		Festival festival1 = createFestival("가나다라");
		Festival festival2 = createFestival("가나다라");
		Festival festival3 = createFestival("가나다라");
		festivalRepository.saveAll(List.of(festival1, festival2, festival3));

		Pageable pageable = PageRequest.of(0, 6);

		// when
		Page<FestivalInfoWithBookmark> festivals = festivalService.getFestivalsByQuery(null, "마바사아", pageable);

		// then
		assertThat(festivals.getContent()).isEmpty();
	}

	@DisplayName("이번 주 페스티벌 조회")
	@Test
	void getFestivalsInThisWeek() {
		// given
		Festival festival1 = createFestival(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 1));
		Festival festival2 = createFestival(LocalDate.of(2023, 12, 30), LocalDate.of(2023, 12, 30));
		Festival festival3 = createFestival(LocalDate.of(2024, 1, 7), LocalDate.of(2024, 1, 10));
		Festival festival4 = createFestival(LocalDate.of(2024, 1, 8), LocalDate.of(2024, 1, 10));

		Pageable pageable = PageRequest.of(0, 3);

		festivalRepository.saveAll(List.of(festival1, festival2, festival3, festival4));

		// when
		Page<FestivalInfo> responses = festivalService.getFestivalsInThisWeek(pageable);

		// then
		assertThat(responses.getContent())
			.hasSize(2)
			.extracting("startDate")
			.containsExactly(
				LocalDate.of(2023, 12, 31),
				LocalDate.of(2024, 1, 7)
			);
	}

	@DisplayName("페스티벌 상세 조회")
	@Test
	void getFestival() {
		// given
		Festival festival = festivalRepository.save(createFestival("부산 락페"));

		List<Category> categories = categoryRepository.saveAll(List.of(createCategory("분야1"), createCategory("분야2")));
		List<Mood> moods = moodRepository.saveAll(List.of(createMood("무드1"), createMood("무드2")));

		List<FestivalCategory> festivalCategories = festivalCategoryRepository.saveAll(
			categories.stream()
				.map(category -> FestivalCategory.builder()
					.categoryId(category.getId())
					.festivalId(festival.getId())
					.build())
				.toList()
		);

		List<FestivalMood> festivalMoods = festivalMoodRepository.saveAll(
			moods.stream()
				.map(mood -> FestivalMood.builder()
					.moodId(mood.getId())
					.festivalId(festival.getId())
					.build())
				.toList()
		);

		FestivalImage image1 = FestivalImage.builder().festivalId(festival.getId()).imageUrl("imageUrl1").build();
		FestivalImage image2 = FestivalImage.builder().festivalId(festival.getId()).imageUrl("imageUrl2").build();

		List<FestivalImage> images = festivalImageRepository.saveAll(List.of(image1, image2));

		List<FestivalBookmark> festivalBookmark = festivalBookmarkRepository.saveAll(
			List.of(FestivalBookmark.of(1L, festival.getId()),
				FestivalBookmark.of(2L, festival.getId()),
				FestivalBookmark.of(3L, festival.getId()))
		);

		FestivalDetailResponse expected = FestivalDetailResponse.builder()
			.festivalId(festival.getId())
			.name(festival.getName())
			.sido(null)
			.sigungu(festival.getSigungu())
			.startDate(festival.getStartDate())
			.endDate(festival.getEndDate())
			.description(festival.getDescription())
			.address(festival.getAddress())
			.tip(festival.getTip())
			.homepageUrl(festival.getHomepageUrl())
			.instagramUrl(festival.getInstagramUrl())
			.fee(festival.getFee())
			.ticketLink(festival.getTicketLink())
			.bookmarkCount(3L)
			.isBookmarked(true)
			.categories(categories.stream().map(CategoryResponse::of).toList())
			.moods(moods.stream().map(MoodResponse::of).toList())
			.images(images.stream().map(FestivalImageResponse::of).toList())
			.build();

		// when
		FestivalDetailResponse actual = festivalService.getFestival(2L, festival.getId());

		// then
		assertThat(actual).usingRecursiveComparison()
			.withComparatorForType(Comparator.comparing(CategoryResponse::getName), CategoryResponse.class)
			.withComparatorForType(Comparator.comparing(MoodResponse::getName), MoodResponse.class)
			.withComparatorForType(Comparator.comparing(FestivalImageResponse::getImageUrl),
				FestivalImageResponse.class)
			.ignoringCollectionOrder()  // 컬렉션의 순서를 무시하고 비교
			.isEqualTo(expected);
	}

	@DisplayName("페스티벌 상세 조회 - 페스티벌의 id를 찾을 수 없으면 에러가 발생한다.")
	@Test
	void getFestival_NotFoundFestival() {
		// given
		final Long INVALID_FESTIVAL_ID = -1L;
		// when
		CustomException exception = assertThrows(CustomException.class
			, () -> festivalService.getFestival(null, INVALID_FESTIVAL_ID));

		// then
		assertEquals(FESTIVAL_NOT_FOUND.getMessage(), exception.getMessage());
	}

	@DisplayName("페스티벌 상세 조회 - 승인되지 않은 페스티벌에 접근할 수 없다.")
	@Test
	void getFestival_PendingFestival() {
		// given
		Festival pendingFestival = festivalRepository.save(createPendingFestival());

		// when
		CustomException exception = assertThrows(CustomException.class
			, () -> festivalService.getFestival(null, pendingFestival.getId()));

		// then
		assertEquals(FESTIVAL_IS_PENDING.getMessage(), exception.getMessage());
	}

	@DisplayName("HOT 한 페스티벌 조회")
	@Test
	void getHotFestivals() {
		// given
		Long userId = 1L;

		Festival festival1 = festivalRepository.save(createFestival("북마크 5"));
		Festival festival2 = festivalRepository.save(createFestival("북마크 3"));
		Festival festival3 = festivalRepository.save(createFestival("북마크 2"));
		Festival festival4 = festivalRepository.save(createFestival("북마크 4"));
		Festival festival5 = festivalRepository.save(createFestival("북마크 0"));
		Festival festival6 = festivalRepository.save(createFestival("북마크 1"));

		for (int i = 0; i < 5; i++) {
			FestivalBookmark bookmark = festivalBookmarkRepository.save(
				createFestivalBookmark(festival1.getId(), userId));
		}

		for (int i = 0; i < 3; i++) {
			FestivalBookmark bookmark = festivalBookmarkRepository.save(
				createFestivalBookmark(festival2.getId(), userId));
		}

		for (int i = 0; i < 2; i++) {
			FestivalBookmark bookmark = festivalBookmarkRepository.save(
				createFestivalBookmark(festival3.getId(), userId));
		}

		for (int i = 0; i < 4; i++) {
			FestivalBookmark bookmark = festivalBookmarkRepository.save(
				createFestivalBookmark(festival4.getId(), userId));
		}

		FestivalBookmark bookmark6 = festivalBookmarkRepository.save(createFestivalBookmark(festival6.getId(), userId));

		// when
		Page<FestivalInfo> hotFestivals = festivalService.getHotFestivals(PageRequest.of(0, 3));

		// then -> 북마크 5, 북마크 4, 북마크 3
		assertEquals(6, hotFestivals.getTotalElements());

		System.out.println(hotFestivals.getContent());

		assertThat(hotFestivals.getContent())
			.hasSize(3)
			.extracting("name")
			.containsExactly(
				"북마크 5",
				"북마크 4",
				"북마크 3"
			);
	}

	@DisplayName("다가오는 페스티벌 조회 - 성공")
	@Test
	void getUpcomingFestival_Success() {
		// given
		User currentUser = userRepository.save(createUser());
		LocalDate today = LocalDate.now();

		Festival festival1 = festivalRepository.save(createFestival(today.plusDays(1), today.plusDays(2)));
		Festival festival2 = festivalRepository.save(createFestival(today.plusDays(3), today.plusDays(4)));
		Festival festival3 = festivalRepository.save(createFestival(today.plusDays(5), today.plusDays(6)));
		Festival festival4 = festivalRepository.save(createFestival(today.plusDays(7), today.plusDays(8)));

		FestivalBookmark bookmark1 = festivalBookmarkRepository.save(
			createFestivalBookmark(festival1.getId(), currentUser.getId()));
		FestivalBookmark bookmark2 = festivalBookmarkRepository.save(
			createFestivalBookmark(festival3.getId(), currentUser.getId()));
		FestivalBookmark bookmark3 = festivalBookmarkRepository.save(
			createFestivalBookmark(festival4.getId(), currentUser.getId()));

		Pageable pageable = PageRequest.of(0, 5);

		// when
		Page<FestivalAndLocation> upcomingFestivals = festivalService.getUpcomingFestival(currentUser.getId(),
			pageable);

		// then
		assertThat(upcomingFestivals.getContent())
			.hasSize(3)
			.extracting("festivalId")
			.containsExactly(
				festival1.getId(),
				festival3.getId(),
				festival4.getId()
			);
	}

	@DisplayName("다가오는 페스티벌 조회 - 현재 날짜 이후의 페스티벌을 조회할 수 있다.")
	@Test
	void getUpcomingFestival_StartDateShouldBeGoeToday() {
		// given
		User currentUser = userRepository.save(createUser());

		Festival startToday = festivalRepository.save(createFestival(today, today.plusDays(2)));
		Festival startedBeforeThreeDay = festivalRepository.save(createFestival(today.minusDays(3), today.plusDays(4)));
		Festival startedBeforeFiveDay = festivalRepository.save(createFestival(today.minusDays(5), today.plusDays(6)));
		Festival startAfter7Day = festivalRepository.save(createFestival(today.plusDays(7), today.plusDays(8)));

		FestivalBookmark bookmark1 = festivalBookmarkRepository.save(
			createFestivalBookmark(startToday.getId(), currentUser.getId()));
		FestivalBookmark bookmark2 = festivalBookmarkRepository.save(
			createFestivalBookmark(startedBeforeFiveDay.getId(), currentUser.getId()));
		FestivalBookmark bookmark3 = festivalBookmarkRepository.save(
			createFestivalBookmark(startAfter7Day.getId(), currentUser.getId()));

		Pageable pageable = PageRequest.of(0, 5);

		// when
		Page<FestivalAndLocation> upcomingFestivals = festivalService.getUpcomingFestival(currentUser.getId(),
			pageable);

		// then
		assertThat(upcomingFestivals.getContent())
			.hasSize(2)
			.extracting("festivalId")
			.containsExactly(
				startToday.getId(), startAfter7Day.getId()
			);

		assertEquals(2, upcomingFestivals.getTotalElements());
	}

	@DisplayName("다가오는 페스티벌 조회 - 종료된 페스티벌은 조회되지 않는다.")
	@Test
	void getUpcomingFestival_NotEndedFestival() {
		// given
		User currentUser = userRepository.save(createUser());

		Festival ended = festivalRepository.save(createFestival(today.minusDays(1), today.minusDays(1)));
		Festival endedBefore7Dat = festivalRepository.save(createFestival(today.minusDays(10), today.minusDays(4)));
		Festival ongoing = festivalRepository.save(createFestival(today, today));

		FestivalBookmark bookmark1 = festivalBookmarkRepository.save(
			createFestivalBookmark(ended.getId(), currentUser.getId()));
		FestivalBookmark bookmark2 = festivalBookmarkRepository.save(
			createFestivalBookmark(endedBefore7Dat.getId(), currentUser.getId()));
		FestivalBookmark bookmark3 = festivalBookmarkRepository.save(
			createFestivalBookmark(ongoing.getId(), currentUser.getId()));

		Pageable pageable = PageRequest.of(0, 5);

		// when
		Page<FestivalAndLocation> upcomingFestivals = festivalService.getUpcomingFestival(currentUser.getId(),
			pageable);

		// then
		assertThat(upcomingFestivals.getContent())
			.hasSize(1)
			.extracting("festivalId")
			.containsExactly(
				ongoing.getId()
			);

		assertEquals(1, upcomingFestivals.getTotalElements());
	}

	@DisplayName("다가오는 페스티벌 조회 - 현재 유저의 정보가 존재해야 한다. ")
	@Test
	void getUpcomingFestival_UserMustBeValid() {
		// given
		Long invalidUserId = -1L;
		Pageable pageable = PageRequest.of(0, 5);

		// when
		CustomException exception = assertThrows(CustomException.class
			, () -> festivalService.getUpcomingFestival(null, pageable));

		// then
		assertEquals(USER_NOT_FOUND.getMessage(), exception.getMessage());
	}

	private static User createUser() {
		return User.builder()
			.roleId(1L)
			.nickname("테스트 유저")
			.build();
	}

	private static FestivalBookmark createFestivalBookmark(Long festvialId, Long userId) {
		return FestivalBookmark.builder()
			.festivalId(festvialId)
			.userId(userId)
			.build();
	}

	private static Festival createFestival() {
		return Festival.builder()
			.userId(1L)
			.name("페스티벌 이름")
			.startDate(LocalDate.of(2024, 10, 4))
			.endDate(LocalDate.of(2024, 10, 4))
			.address("페스티벌 주소")
			.sidoId(1L)
			.sigungu("시군구")
			.latitude(10.1)
			.longitude(10.1)
			.tip("페스티벌 팁")
			.homepageUrl("홈페이지 url")
			.instagramUrl("인스타그램 url")
			.fee("비용")
			.description("페스티벌 상세 설명")
			.ticketLink("티켓 링크")
			.playtime("페스티벌 진행 시간")
			.isPending(false)
			.build();
	}

	private static Festival createFestival(LocalDate startDate, LocalDate endDate, Long sidoId) {
		return Festival.builder()
			.userId(1L)
			.name("페스티벌 이름")
			.startDate(startDate)
			.endDate(endDate)
			.address("페스티벌 주소")
			.sidoId(sidoId)
			.sigungu("시군구")
			.latitude(10.1)
			.longitude(10.1)
			.tip("페스티벌 팁")
			.homepageUrl("홈페이지 url")
			.instagramUrl("인스타그램 url")
			.fee("비용")
			.description("페스티벌 상세 설명")
			.ticketLink("티켓 링크")
			.playtime("페스티벌 진행 시간")
			.isPending(false)
			.build();
	}

	private static Festival createFestival(LocalDate startDate, LocalDate endDate) {
		return Festival.builder()
			.userId(1L)
			.name("페스티벌 이름")
			.startDate(startDate)
			.endDate(endDate)
			.address("페스티벌 주소")
			.sidoId(1L)
			.sigungu("시군구")
			.latitude(10.1)
			.longitude(10.1)
			.tip("페스티벌 팁")
			.homepageUrl("홈페이지 url")
			.instagramUrl("인스타그램 url")
			.fee("비용")
			.description("페스티벌 상세 설명")
			.ticketLink("티켓 링크")
			.playtime("페스티벌 진행 시간")
			.isPending(false)
			.build();
	}

	private static Festival createFestival(String name, LocalDate currentDate, Double latitude, Double longitude) {
		return Festival.builder()
			.userId(1L)
			.name(name)
			.startDate(currentDate)
			.endDate(currentDate)
			.address("페스티벌 주소")
			.sidoId(1L)
			.sigungu("시군구")
			.latitude(latitude)
			.longitude(longitude)
			.tip("페스티벌 팁")
			.homepageUrl("홈페이지 url")
			.instagramUrl("인스타그램 url")
			.fee("비용")
			.description("페스티벌 상세 설명")
			.ticketLink("티켓 링크")
			.playtime("페스티벌 진행 시간")
			.isPending(false)
			.build();
	}

	private static Festival createFestival(String name) {
		return Festival.builder()
			.userId(1L)
			.name(name)
			.startDate(LocalDate.of(2024, 1, 1))
			.endDate(LocalDate.of(2024, 1, 10))
			.address("페스티벌 주소")
			.sidoId(1L)
			.sigungu("시군구")
			.latitude(10.1)
			.longitude(10.1)
			.tip("페스티벌 팁")
			.homepageUrl("홈페이지 url")
			.instagramUrl("인스타그램 url")
			.fee("비용")
			.description("페스티벌 상세 설명")
			.ticketLink("티켓 링크")
			.playtime("페스티벌 진행 시간")
			.isPending(false)
			.build();
	}

	private static Festival createPendingFestival() {
		return Festival.builder()
			.userId(1L)
			.name("페스티벌 이름")
			.startDate(LocalDate.of(2024, 1, 1))
			.endDate(LocalDate.of(2024, 1, 10))
			.address("페스티벌 주소")
			.sidoId(1L)
			.sigungu("시군구")
			.latitude(10.1)
			.longitude(10.1)
			.tip("페스티벌 팁")
			.homepageUrl("홈페이지 url")
			.instagramUrl("인스타그램 url")
			.fee("비용")
			.description("페스티벌 상세 설명")
			.ticketLink("티켓 링크")
			.playtime("페스티벌 진행 시간")
			.isPending(true)
			.build();
	}

	private static Category createCategory(String name) {
		return Category.builder()
			.name(name)
			.emoji("이모지")
			.build();
	}

	private static Mood createMood(String name) {
		return Mood.builder()
			.name(name)
			.build();
	}

	private static Sido createSido() {
		return Sido.builder()
			.name("부산")
			.code(42)
			.build();
	}
}
