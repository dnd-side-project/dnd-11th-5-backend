package com.odiga.fiesta.festival.service;

import static com.odiga.fiesta.common.error.ErrorCode.*;
import static com.odiga.fiesta.festival.domain.Festival.*;
import static java.util.stream.Collectors.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.dto.response.DailyFestivalContents;
import com.odiga.fiesta.festival.dto.response.FestivalMonthlyResponse;
import com.odiga.fiesta.festival.dto.response.FestivalSimpleResponse;
import com.odiga.fiesta.festival.repository.FestivalRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FestivalService {

	private final FestivalRepository festivalRepository;

	public FestivalMonthlyResponse getMonthlyFestivals(int year, int month) {
		validateMonth(month);

		YearMonth yearMonth = YearMonth.of(year, month);
		LocalDate startOfMonth = yearMonth.atDay(1);
		LocalDate endOfMonth = yearMonth.atEndOfMonth();

		// 날짜 기준 그룹화, 필터링
		List<Festival> festivals = festivalRepository.findFestivalsWithinDateRange(
			startOfMonth, endOfMonth);

		Map<LocalDate, List<Festival>> groupedByDate = getFilteredGroupedByDate(festivals, startOfMonth, endOfMonth);
		List<LocalDate> allDatesInMonth = getAllDatesInMonth(startOfMonth, endOfMonth);

		List<DailyFestivalContents> dailyContents = getDailyContents(allDatesInMonth, groupedByDate);

		return FestivalMonthlyResponse.builder()
			.year(year)
			.month(month)
			.contents(dailyContents)
			.build();
	}

	private static List<LocalDate> getAllDatesInMonth(LocalDate startOfMonth, LocalDate endOfMonth) {
		return startOfMonth
			.datesUntil(endOfMonth.plusDays(1))
			.toList();
	}

	private static Map<LocalDate, List<Festival>> getFilteredGroupedByDate(List<Festival> festivals,
		LocalDate startOfMonth,
		LocalDate endOfMonth) {
		return getGroupedByDate(festivals).entrySet().stream()
			.filter(
				entry -> !entry.getKey().isBefore(startOfMonth) && !entry.getKey().isAfter(endOfMonth))
			.collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	private static List<DailyFestivalContents> getDailyContents(List<LocalDate> allDatesInMonth,
		Map<LocalDate, List<Festival>> groupedByDate) {
		return allDatesInMonth.stream()
			.map(date -> DailyFestivalContents.builder()
				.date(date)
				.festivals(groupedByDate.getOrDefault(date, List.of()).stream()
					.map(FestivalSimpleResponse::of)
					.limit(3)
					.collect(toList()))
				.totalElements(groupedByDate.getOrDefault(date, List.of()).size())
				.build())
			.toList();
	}

	private void validateMonth(int month) {
		if (month < 1 || month > 12) {
			throw new CustomException(INVALID_FESTIVAL_MONTH);
		}
	}
}
