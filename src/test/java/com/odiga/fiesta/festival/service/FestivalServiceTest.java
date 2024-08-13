package com.odiga.fiesta.festival.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.odiga.fiesta.IntegrationTestSupport;
import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.domain.FestivalImage;
import com.odiga.fiesta.festival.dto.response.DailyFestivalContents;
import com.odiga.fiesta.festival.dto.response.FestivalInfoResponse;
import com.odiga.fiesta.festival.dto.response.FestivalMonthlyResponse;
import com.odiga.fiesta.festival.repository.FestivalImageRepository;
import com.odiga.fiesta.festival.repository.FestivalRepository;
import com.odiga.fiesta.sido.domain.Sido;
import com.odiga.fiesta.sido.repository.SidoRepository;

class FestivalServiceTest extends IntegrationTestSupport {

	@Autowired
	private FestivalService festivalService;

	@Autowired
	private FestivalRepository festivalRepository;

	@Autowired
	private FestivalImageRepository festivalImageRepository;

	@Autowired
	private SidoRepository sidoRepository;

	@DisplayName("startDate 와 endDate 사이에 해당 월이 끼어있어도 페스티벌이 포함되어야 한다.")
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

		assertEquals(yearMonth.lengthOfMonth(), contents.size()); // 4월의 일 수와 Content의 크기 비교

		List<LocalDate> allDatesInApril = Stream.iterate(yearMonth.atDay(1), date -> date.plusDays(1))
			.limit(yearMonth.lengthOfMonth())
			.toList();

		for (int i = 0; i < contents.size(); i++) {
			DailyFestivalContents dailyContent = contents.get(i);
			assertEquals(allDatesInApril.get(i), dailyContent.getDate()); // 날짜 검증
			assertEquals(1, dailyContent.getFestivals().size()); // 페스티벌 크기 검증
			assertEquals(festival1.getName(), dailyContent.getFestivals().get(0).getName()); // 페스티벌 이름 검증
			assertEquals(1, dailyContent.getTotalElements()); // totalElements 검증
		}
	}

	@DisplayName("데이터가 3개 이상일 경우, 3개의 데이터만 보인다.")
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

	@DisplayName("해당 일자에 개최되고 있는 페스티벌을 조회할 수 있다.")
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
		Page<FestivalInfoResponse> result = festivalService.getFestivalsByDay(null, 2024, 10, 4, pageable);

		// then
		assertThat(result.getContent()).hasSize(1)
			.extracting("festivalId", "name", "sido", "sigungu", "thumbnailImage",
				"startDate", "endDate", "isBookmarked")
			.containsExactlyInAnyOrder(
				tuple(savedFestival1.getId(), savedFestival1.getName(), savedSido.getName(), savedFestival1.getSigungu(),
					savedImage1.getImageUrl(), savedFestival1.getStartDate(), savedFestival1.getEndDate(), false)
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

	private static Sido createSido() {
		return Sido.builder()
			.name("부산")
			.code(42)
			.build();
	}
}
