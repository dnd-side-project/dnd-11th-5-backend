package com.odiga.fiesta.festival.service;

import static com.odiga.fiesta.common.error.ErrorCode.*;
import static com.odiga.fiesta.festival.domain.Festival.*;
import static java.util.stream.Collectors.*;

import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odiga.fiesta.category.repository.CategoryRepository;
import com.odiga.fiesta.common.PageResponse;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.common.util.RedisUtils;
import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.dto.projection.FestivalWithBookmarkAndSido;
import com.odiga.fiesta.festival.dto.request.FestivalFilterCondition;
import com.odiga.fiesta.festival.dto.request.FestivalFilterRequest;
import com.odiga.fiesta.festival.dto.response.DailyFestivalContents;
import com.odiga.fiesta.festival.dto.response.FestivalBasic;
import com.odiga.fiesta.festival.dto.response.FestivalInfo;
import com.odiga.fiesta.festival.dto.response.FestivalMonthlyResponse;
import com.odiga.fiesta.festival.repository.FestivalImageRepository;
import com.odiga.fiesta.festival.repository.FestivalRepository;
import com.odiga.fiesta.sido.repository.SidoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FestivalService {

	private final Clock clock;

	private final CategoryRepository categoryRepository;
	private final SidoRepository sidoRepository;

	private final FestivalRepository festivalRepository;
	private final FestivalImageRepository festivalImageRepository;

	private final RedisUtils redisUtils;

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

	public Page<FestivalInfo> getFestivalsByDay(Long userId, int year, int month, int day,
		Pageable pageable) {
		validateFestivalDay(year, month, day);

		LocalDate date = LocalDate.of(year, month, day);

		Page<FestivalWithBookmarkAndSido> festivals = festivalRepository.findFestivalsInDate(date,
			pageable, userId);

		List<FestivalInfo> responses = getFestivalWithBookmarkAndSidoAndThumbnailImage(festivals);

		return new PageImpl<>(responses, pageable, festivals.getTotalElements());
	}

	public Page<FestivalInfo> getFestivalByFiltersAndSort(Long userId,
		FestivalFilterRequest festivalFilterRequest,
		Double latitude, Double longitude, Pageable pageable) {

		FestivalFilterCondition festivalFilterCondition = getFestivalFilterCondition(festivalFilterRequest);

		LocalDate date = LocalDate.now(clock);
		Page<FestivalWithBookmarkAndSido> festivalsByFilters = festivalRepository.findFestivalsByFiltersAndSort(userId,
			festivalFilterCondition, latitude, longitude, date, pageable);

		List<FestivalInfo> responses = getFestivalWithBookmarkAndSidoAndThumbnailImage(festivalsByFilters);

		return new PageImpl<>(responses, pageable, festivalsByFilters.getTotalElements());
	}

	public Page<FestivalInfo> getFestivalsByQuery(Long userId, String query, Pageable pageable) {
		Page<FestivalWithBookmarkAndSido> festivalsByQuery = festivalRepository.findFestivalsByQuery(userId, query,
			pageable);

		List<FestivalInfo> responses = getFestivalWithBookmarkAndSidoAndThumbnailImage(festivalsByQuery);

		return new PageImpl<>(responses, pageable, festivalsByQuery.getTotalElements());
	}

	@Transactional
	public void updateSearchRanking(String rankingKey, FestivalBasic searchItem) {
		log.warn("update search raking, {}", searchItem);

		final Double SCORE_INCREMENT_AMOUNT = 1.0;

		String fetivalIdToString = searchItem.getFestivalId().toString();

		Double currentScore = redisUtils.zScore(rankingKey, fetivalIdToString);
		Double newScore = (currentScore != null) ? currentScore + SCORE_INCREMENT_AMOUNT : SCORE_INCREMENT_AMOUNT;

		redisUtils.zAdd(rankingKey, fetivalIdToString, newScore);

		log.warn("item = {}, score = {} -> {}", searchItem.getName(), currentScore, newScore);
	}

	@Transactional
	public PageResponse<FestivalBasic> getTrendingFestival(String rankingKey, Long page, Integer size) {
		Set<ZSetOperations.TypedTuple<String>> set = redisUtils.zRevrange(rankingKey, page * size,
			(page * size) + size);

		log.error("set - {}" , set);
		List<FestivalBasic> festivals = festivalRepository.findAllById(
				set.stream().map(tuple -> Long.parseLong(tuple.getValue())).toList())
			.stream().map(FestivalBasic::of).toList();

		return new PageResponse(festivals, 0L, size, page.intValue(),
			redisUtils.zSize(rankingKey),
			(page.intValue() / size) + 1);
	}

	private List<FestivalInfo> getFestivalWithBookmarkAndSidoAndThumbnailImage(
		Page<FestivalWithBookmarkAndSido> festivalsByFilters) {
		return festivalsByFilters.getContent().stream().map(festival -> {
			String thumbnailImage = festivalImageRepository.findImageUrlByFestivalId(festival.getFestivalId());
			return FestivalInfo.of(festival, thumbnailImage);
		}).toList();
	}

	// 필터링을 위해, request list 내부의 중복 제거
	private FestivalFilterCondition getFestivalFilterCondition(FestivalFilterRequest festivalFilterRequest) {
		Optional.ofNullable(festivalFilterRequest.getMonths())
			.orElse(Collections.emptyList())
			.forEach(this::validateMonth);

		Optional.ofNullable(festivalFilterRequest.getAreas())
			.orElse(Collections.emptyList())
			.forEach(this::validateAreaId);

		Optional.ofNullable(festivalFilterRequest.getCategories())
			.orElse(Collections.emptyList())
			.forEach(this::validateFestivalCategory);

		Set<Long> areas = new HashSet<>(
			Optional.ofNullable(festivalFilterRequest.getAreas()).orElse(Collections.emptyList()));
		Set<Integer> months = new HashSet<>(
			Optional.ofNullable(festivalFilterRequest.getMonths()).orElse(Collections.emptyList()));
		Set<Long> categories = new HashSet<>(
			Optional.ofNullable(festivalFilterRequest.getCategories()).orElse(Collections.emptyList()));

		return FestivalFilterCondition.builder()
			.areas(areas)
			.months(months)
			.categories(categories)
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
					.map(FestivalBasic::of)
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

	private void validateAreaId(Long areaId) {
		sidoRepository.findById(areaId).orElseThrow(() -> new CustomException(FESTIVAL_AREA_NOT_FOUND));
	}

	private void validateFestivalCategory(Long festivalId) {
		categoryRepository.findById(festivalId).orElseThrow(() -> new CustomException(FESTIVAL_CATEGORY_NOT_FOUND));
	}

	private void validateFestivalDay(int year, int month, int day) {
		YearMonth yearMonth = YearMonth.of(year, month);

		if (!yearMonth.isValidDay(day)) {
			throw new CustomException(INVALID_FESTIVAL_DATE);
		}
	}

}
