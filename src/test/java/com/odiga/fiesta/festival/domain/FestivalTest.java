package com.odiga.fiesta.festival.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FestivalTest {

	@DisplayName("단일 페스티벌을 날짜별로 그룹핑하면 시작일부터 종료일까지 포함해야 한다")
	@Test
	void getGroupedByDate_singleFestival() {

		// given
		Festival festival = createFestival(
			LocalDate.of(2024, 10, 23),
			LocalDate.of(2024, 10, 25)
		);

		List<Festival> festivals = Collections.singletonList(festival);

		Map<LocalDate, List<Festival>> expected = Map.of(
			LocalDate.of(2024, 10, 23), Collections.singletonList(festival),
			LocalDate.of(2024, 10, 24), Collections.singletonList(festival),
			LocalDate.of(2024, 10, 25), Collections.singletonList(festival)
		);

		// when
		Map<LocalDate, List<Festival>> result = Festival.getGroupedByDate(festivals);

		// then
		assertThat(result).hasSize(3)
			.usingRecursiveComparison()
			.isEqualTo(expected);
	}

	@DisplayName("여러 개의 페스티벌을 날짜별로 그룹핑하면 동일한 날짜에 개최된 페스티벌을 모두 포함해야 한다")
	@Test
	void getGroupedByDate_multipleFestival()  {
		// given
		Festival festival1 = createFestival(
			LocalDate.of(2024, 10, 24),
			LocalDate.of(2024, 10, 24)
		);

		Festival festival2 = createFestival(
			LocalDate.of(2024, 10, 24),
			LocalDate.of(2024, 10, 24)
		);

		List<Festival> festivals = Arrays.asList(festival1, festival2);

		Map<LocalDate, List<Festival>> expected = Map.of(
			LocalDate.of(2024, 10, 24), Arrays.asList(festival1, festival2)
		);

		// when
		Map<LocalDate, List<Festival>> result = Festival.getGroupedByDate(festivals);

		// then
		assertThat(result).hasSize(1)
			.usingRecursiveComparison()
			.isEqualTo(expected);
	}

	@DisplayName("여러 개의 페스티벌이 기간이 겹칠 때 그룹핑하면 모든 겹치는 날짜들을 포함해야 한다")
	@Test
	void getGroupedByDate_overlappingFestivals()  {
		// given
		Festival festival1 = createFestival(
			LocalDate.of(2024, 10, 23),
			LocalDate.of(2024, 10, 25)
		);

		Festival festival2 = createFestival(
			LocalDate.of(2024, 10, 24),
			LocalDate.of(2024, 10, 25)
		);

		Festival festival3 = createFestival(
			LocalDate.of(2024, 10, 23),
			LocalDate.of(2024, 10, 26)
		);

		List<Festival> festivals = Arrays.asList(festival1, festival2, festival3);

		Map<LocalDate, List<Festival>> expected = Map.of(
			LocalDate.of(2024, 10, 23), Arrays.asList(festival1, festival3),
			LocalDate.of(2024, 10, 24), Arrays.asList(festival1, festival2, festival3),
			LocalDate.of(2024, 10, 25), Arrays.asList(festival1, festival2, festival3),
			LocalDate.of(2024, 10, 26), Collections.singletonList(festival3)
		);

		// when
		Map<LocalDate, List<Festival>> result = Festival.getGroupedByDate(festivals);

		// then
		assertThat(result).hasSize(4)
			.usingRecursiveComparison()
			.isEqualTo(expected);
	}

	private static Festival createFestival(LocalDate startDate, LocalDate endDate) {
		return Festival.builder()
			.id(1L)
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

}
