package com.odiga.fiesta.festival.service;

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
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.odiga.fiesta.common.util.RedisUtils;
import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.domain.FestivalImage;
import com.odiga.fiesta.festival.dto.request.FestivalFilterRequest;
import com.odiga.fiesta.festival.dto.response.DailyFestivalContents;
import com.odiga.fiesta.festival.dto.response.FestivalBasic;
import com.odiga.fiesta.festival.dto.response.FestivalInfo;
import com.odiga.fiesta.festival.dto.response.FestivalMonthlyResponse;
import com.odiga.fiesta.festival.dto.response.FestivalThisWeekResponse;
import com.odiga.fiesta.festival.repository.FestivalImageRepository;
import com.odiga.fiesta.festival.repository.FestivalRepository;
import com.odiga.fiesta.sido.domain.Sido;
import com.odiga.fiesta.sido.repository.SidoRepository;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class FestivalServiceTest {

	private static final Clock CURRENT_CLOCK = Clock.fixed(Instant.parse("2024-01-01T10:00:00Z"), ZoneOffset.UTC);

	@Autowired
	private FestivalService festivalService;

	@Autowired
	private FestivalRepository festivalRepository;

	@Autowired
	private FestivalImageRepository festivalImageRepository;

	@Autowired
	private SidoRepository sidoRepository;

	@SpyBean
	private Clock clock;

	@BeforeEach
	void beforeEach() {
		doReturn(Instant.now(CURRENT_CLOCK))
			.when(clock)
			.instant();
	}

	@Autowired
	private RedisTemplate redisTemplate;

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
		Page<FestivalInfo> result = festivalService.getFestivalsByDay(null, 2024, 10, 4, pageable);

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
		Page<FestivalInfo> responses = festivalService.getFestivalByFiltersAndSort(null,
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
		Page<FestivalInfo> responses = festivalService.getFestivalByFiltersAndSort(null,
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
		Page<FestivalInfo> festivals = festivalService.getFestivalsByQuery(null, "펜타", pageable);

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
		Page<FestivalInfo> festivals = festivalService.getFestivalsByQuery(null, "마바사아", pageable);

		// then
		assertThat(festivals.getContent()).isEmpty();
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
		Page<FestivalThisWeekResponse> responses = festivalService.getFestivalsInThisWeek(pageable);

		// then
		assertThat(responses.getContent())
			.hasSize(2)
			.extracting("startDate")
			.containsExactly(
				LocalDate.of(2023, 12, 31),
				LocalDate.of(2024, 1, 7)
			);
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

	private static Sido createSido() {
		return Sido.builder()
			.name("부산")
			.code(42)
			.build();
	}
}
