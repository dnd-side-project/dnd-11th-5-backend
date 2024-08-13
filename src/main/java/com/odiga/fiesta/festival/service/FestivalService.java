package com.odiga.fiesta.festival.service;

import static com.odiga.fiesta.common.error.ErrorCode.*;
import static com.odiga.fiesta.festival.domain.Festival.*;
import static java.util.stream.Collectors.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.dto.projection.FestivalWithBookmarkAndSido;
import com.odiga.fiesta.festival.dto.response.DailyFestivalContents;
import com.odiga.fiesta.festival.dto.response.FestivalMonthlyResponse;
import com.odiga.fiesta.festival.dto.response.FestivalBasicResponse;
import com.odiga.fiesta.festival.dto.response.FestivalInfoResponse;
import com.odiga.fiesta.festival.repository.FestivalImageRepository;
import com.odiga.fiesta.festival.repository.FestivalRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FestivalService {

	private final FestivalRepository festivalRepository;
	private final FestivalImageRepository festivalImageRepository;

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

	public Page<FestivalInfoResponse> getFestivalsByDay(Long userId, int year, int month, int day,
		Pageable pageable) {
		validateFestivalDay(year, month, day);

		LocalDate date = LocalDate.of(year, month, day);

		Page<FestivalWithBookmarkAndSido> festivals = festivalRepository.findFestivalsInDate(date,
			pageable, userId);

		List<FestivalInfoResponse> responses = festivals.getContent().stream().map(festival -> {
			String thumbnailImage = festivalImageRepository.findImageUrlByFestivalId(festival.getFestivalId());
			return FestivalInfoResponse.of(festival, thumbnailImage);
		}).toList();

		return new PageImpl<>(responses, pageable, festivals.getTotalElements());
	}

	private void validateFestivalDay(int year, int month, int day) {
		YearMonth yearMonth = YearMonth.of(year, month);

		if (!yearMonth.isValidDay(day)) {
			throw new CustomException(INVALID_FESTIVAL_DATE);
		}
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
					.map(FestivalBasicResponse::of)
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
