package com.odiga.fiesta.festival.service;

import static com.odiga.fiesta.common.error.ErrorCode.*;
import static com.odiga.fiesta.festival.domain.Festival.*;
import static com.odiga.fiesta.festival.util.CityMapper.*;
import static java.util.Objects.*;
import static java.util.stream.Collectors.*;

import java.io.IOException;
import java.time.Clock;
import java.time.DayOfWeek;
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
import org.springframework.web.multipart.MultipartFile;

import com.odiga.fiesta.badge.service.BadgeService;
import com.odiga.fiesta.category.repository.CategoryRepository;
import com.odiga.fiesta.common.PageResponse;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.common.util.FileUtils;
import com.odiga.fiesta.common.util.RedisUtils;
import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.domain.FestivalCategory;
import com.odiga.fiesta.festival.domain.FestivalImage;
import com.odiga.fiesta.festival.domain.FestivalModificationRequest;
import com.odiga.fiesta.festival.domain.FestivalMood;
import com.odiga.fiesta.festival.dto.projection.FestivalData;
import com.odiga.fiesta.festival.dto.projection.FestivalDetailData;
import com.odiga.fiesta.festival.dto.projection.FestivalWithBookmarkAndSido;
import com.odiga.fiesta.festival.dto.projection.FestivalWithSido;
import com.odiga.fiesta.festival.dto.request.FestivalCreateRequest;
import com.odiga.fiesta.festival.dto.request.FestivalFilterCondition;
import com.odiga.fiesta.festival.dto.request.FestivalFilterRequest;
import com.odiga.fiesta.festival.dto.request.CreateFestivalModificationRequest;
import com.odiga.fiesta.festival.dto.response.CategoryResponse;
import com.odiga.fiesta.festival.dto.response.DailyFestivalContents;
import com.odiga.fiesta.festival.dto.response.FestivalAndLocation;
import com.odiga.fiesta.festival.dto.response.FestivalBasic;
import com.odiga.fiesta.festival.dto.response.FestivalDetailResponse;
import com.odiga.fiesta.festival.dto.response.FestivalImageResponse;
import com.odiga.fiesta.festival.dto.response.FestivalInfo;
import com.odiga.fiesta.festival.dto.response.FestivalInfoWithBookmark;
import com.odiga.fiesta.festival.dto.response.FestivalModificationResponse;
import com.odiga.fiesta.festival.dto.response.FestivalMonthlyResponse;
import com.odiga.fiesta.festival.dto.response.MoodResponse;
import com.odiga.fiesta.festival.dto.response.RecommendFestivalResponse;
import com.odiga.fiesta.festival.repository.FestivalCategoryRepository;
import com.odiga.fiesta.festival.repository.FestivalImageRepository;
import com.odiga.fiesta.festival.repository.FestivalModificationRequestRepository;
import com.odiga.fiesta.festival.repository.FestivalMoodRepository;
import com.odiga.fiesta.festival.repository.FestivalRepository;
import com.odiga.fiesta.festival.repository.FestivalUserTypeRepository;
import com.odiga.fiesta.mood.repository.MoodRepository;
import com.odiga.fiesta.sido.repository.SidoRepository;
import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.domain.UserType;
import com.odiga.fiesta.user.dto.response.UserTypeResponse;
import com.odiga.fiesta.user.repository.UserRepository;
import com.odiga.fiesta.user.repository.UserTypeRepository;
import com.odiga.fiesta.user.service.UserTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FestivalService {

	private final FestivalUserTypeRepository festivalUserTypeRepository;
	private final UserRepository userRepository;
	private final UserTypeRepository userTypeRepository;

	private final Clock clock;

	private final CategoryRepository categoryRepository;
	private final SidoRepository sidoRepository;
	private final MoodRepository moodRepository;

	private final FestivalRepository festivalRepository;
	private final FestivalImageRepository festivalImageRepository;
	private final FestivalCategoryRepository festivalCategoryRepository;
	private final FestivalMoodRepository festivalMoodRepository;
	private final FestivalModificationRequestRepository festivalModificationRequestRepository;

	private final UserTypeService userTypeService;
	private final BadgeService badgeService;

	private final RedisUtils redisUtils;
	private final FileUtils fileUtils;

	private static final String FESTIVAL_DIR_NAME = "festival";

	@Transactional
	public FestivalBasic createFestival(final Long userId, final FestivalCreateRequest request
		, final List<MultipartFile> files) {

		validateUserId(userId);
		validateCityName(request.getSido());
		validateFileExtension(files);
		validateFileCount(files);

		Long sidoId = getIdFromCityName(request.getSido());
		Festival festival = festivalRepository.save(of(request, userId, sidoId));

		saveTopUserTypesForFestival(request, festival);
		createFestivalImages(files, festival);

		saveFestivalCategory(request.getCategoryIds(), festival);
		saveFestivalMood(request.getMoodIds(), festival);

		badgeService.giveFestivalBadge(userId);
		return FestivalBasic.of(festival);
	}

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

	public Page<FestivalInfoWithBookmark> getFestivalsByDay(Long userId, int year, int month, int day,
		Pageable pageable) {
		validateFestivalDay(year, month, day);

		LocalDate date = LocalDate.of(year, month, day);

		Page<FestivalWithBookmarkAndSido> festivals = festivalRepository.findFestivalsInDate(date,
			pageable, userId);

		List<FestivalInfoWithBookmark> responses = getFestivalWithBookmarkAndSidoAndThumbnailImage(festivals);

		return new PageImpl<>(responses, pageable, festivals.getTotalElements());
	}

	public Page<FestivalInfoWithBookmark> getFestivalByFiltersAndSort(Long userId,
		FestivalFilterRequest festivalFilterRequest,
		Double latitude, Double longitude, Pageable pageable) {

		FestivalFilterCondition festivalFilterCondition = getFestivalFilterCondition(festivalFilterRequest);

		LocalDate date = LocalDate.now(clock);
		Page<FestivalWithBookmarkAndSido> festivalsByFilters = festivalRepository.findFestivalsByFiltersAndSort(userId,
			festivalFilterCondition, latitude, longitude, date, pageable);

		List<FestivalInfoWithBookmark> responses = getFestivalWithBookmarkAndSidoAndThumbnailImage(festivalsByFilters);
		return new PageImpl<>(responses, pageable, festivalsByFilters.getTotalElements());
	}

	public Page<FestivalInfoWithBookmark> getFestivalsByQuery(Long userId, String query, Pageable pageable) {
		Page<FestivalWithBookmarkAndSido> festivalsByQuery = festivalRepository.findFestivalsByQuery(userId, query,
			pageable);

		List<FestivalInfoWithBookmark> responses = getFestivalWithBookmarkAndSidoAndThumbnailImage(festivalsByQuery);
		return new PageImpl<>(responses, pageable, festivalsByQuery.getTotalElements());
	}

	@Transactional
	public void updateSearchRanking(String rankingKey, FestivalBasic searchItem) {
		final Double SCORE_INCREMENT_AMOUNT = 1.0;

		String fetivalIdToString = searchItem.getFestivalId().toString();

		Double currentScore = redisUtils.zScore(rankingKey, fetivalIdToString);
		Double newScore = (currentScore != null) ? currentScore + SCORE_INCREMENT_AMOUNT : SCORE_INCREMENT_AMOUNT;

		redisUtils.zAdd(rankingKey, fetivalIdToString, newScore);
	}

	@Transactional
	public PageResponse<FestivalBasic> getTrendingFestival(String rankingKey, Long page, Integer size) {
		Set<ZSetOperations.TypedTuple<String>> set = redisUtils.zRevrange(rankingKey, page * size,
			(page * size) + size);

		List<FestivalBasic> festivals = festivalRepository.findAllById(
				set.stream().map(tuple -> Long.parseLong(tuple.getValue())).toList())
			.stream().map(FestivalBasic::of).toList();

		return new PageResponse(festivals, 0L, size, page.intValue(),
			redisUtils.zSize(rankingKey),
			(page.intValue() / size) + 1);
	}

	public Page<FestivalInfoWithBookmark> getBookmarkedFestivals(User user, Pageable pageable) {
		validateUserId(user.getId());

		Page<FestivalWithBookmarkAndSido> bookmarkedFestivals = festivalRepository.findBookmarkedFestivals(user.getId(),
			pageable);

		List<FestivalInfoWithBookmark> festivals = getFestivalWithBookmarkAndSidoAndThumbnailImage(
			bookmarkedFestivals);

		return new PageImpl<>(festivals, pageable, bookmarkedFestivals.getTotalElements());
	}

	private List<FestivalInfoWithBookmark> getFestivalWithBookmarkAndSidoAndThumbnailImage(
		Page<FestivalWithBookmarkAndSido> festivalsByFilters) {
		// 1. 페스티벌 아이디를 가져온다.
		List<Long> festialIdList = festivalsByFilters.getContent()
			.stream()
			.map(FestivalWithBookmarkAndSido::getFestivalId)
			.toList();

		// 2. 페스티벌 아이디를 이용해 이미지를 가져온다.
		Map<Long, String> festivalIdToThumbnailImage = festivalRepository.findThumbnailImageByFestivalId(festialIdList);

		return festivalsByFilters.getContent().stream().map(festival -> {
			String thumbnailImage = festivalIdToThumbnailImage.get(festival.getFestivalId());
			return FestivalInfoWithBookmark.of(festival, thumbnailImage);
		}).toList();
	}

	public Page<FestivalInfo> getFestivalsInThisWeek(Pageable pageable) {

		LocalDate now = LocalDate.now(clock);
		LocalDate startDayOfWeek = now.with(DayOfWeek.MONDAY);
		LocalDate endDayOfWeek = now.with(DayOfWeek.SUNDAY);

		Page<FestivalWithSido> festivals = festivalRepository.findFestivalsAndSidoWithinDateRange(startDayOfWeek,
			endDayOfWeek, pageable);
		List<FestivalInfo> responses = getFestivalAndSidoWithThumbnailImage(festivals);
		return new PageImpl<>(responses, pageable, festivals.getTotalElements());
	}

	public Page<FestivalInfo> getHotFestivals(Pageable pageable) {

		LocalDate date = LocalDate.now(clock);
		Page<FestivalWithSido> festivals = festivalRepository.findMostLikeFestival(pageable, date);

		List<FestivalInfo> responses = getFestivalAndSidoWithThumbnailImage(festivals);

		return new PageImpl<>(responses, pageable, festivals.getTotalElements());
	}

	public Page<FestivalAndLocation> getUpcomingFestival(Long userId, Pageable pageable) {
		validateUserId(userId);
		LocalDate date = LocalDate.now(clock);
		Page<FestivalAndLocation> festivals = festivalRepository.findUpcomingFestivalAndLocation(userId, date,
			pageable);
		return new PageImpl<>(festivals.getContent(), pageable, festivals.getTotalElements());
	}

	private List<FestivalInfo> getFestivalAndSidoWithThumbnailImage(
		List<FestivalWithSido> festivals) {

		List<Long> festivalIds = festivals.stream().map(FestivalData::getFestivalId).toList();
		Map<Long, String> festivalIdToThumbnailImage = festivalRepository.findThumbnailImageByFestivalId(festivalIds);

		return festivals.stream().map(festival -> {
			String thumbnailImage = festivalIdToThumbnailImage.get(festival.getFestivalId());
			return FestivalInfo.of(festival, thumbnailImage);
		}).toList();
	}

	private List<FestivalInfo> getFestivalAndSidoWithThumbnailImage(
		Page<FestivalWithSido> festivals) {

		List<Long> festivalIds = festivals.stream().map(FestivalData::getFestivalId).toList();
		Map<Long, String> festivalIdToThumbnailImage = festivalRepository.findThumbnailImageByFestivalId(festivalIds);

		return festivals.getContent().stream().map(festival -> {
			String thumbnailImage = festivalIdToThumbnailImage.get(festival.getFestivalId());
			return FestivalInfo.of(festival, thumbnailImage);
		}).toList();
	}

	public FestivalDetailResponse getFestival(Long userId, Long festivalId) {
		validateFestival(festivalId);

		FestivalDetailData festivalDetail = festivalRepository.findFestivalDetail(userId, festivalId)
			.orElseThrow(() -> new CustomException(FESTIVAL_NOT_FOUND));

		List<Long> festivalCategoryIds = festivalCategoryRepository.findAllCategoryIdByFestivalId(festivalId);
		List<Long> festivalMoodIds = festivalMoodRepository.findAllMoodIdByFestivalId(festivalId);

		List<FestivalImageResponse> images = festivalImageRepository.findAllByFestivalId(festivalId)
			.stream().map(FestivalImageResponse::of).toList();
		List<CategoryResponse> categories = categoryRepository.findByIdIn(festivalCategoryIds)
			.stream().map(CategoryResponse::of).toList();
		List<MoodResponse> moods = moodRepository.findByIdIn(festivalMoodIds)
			.stream().map(MoodResponse::of).toList();

		return FestivalDetailResponse.of(festivalDetail, categories, moods, images);
	}

	public RecommendFestivalResponse getRecommendFestivals(User user, Long size) {
		validateUserId(user.getId());

		if (isNull(user.getUserTypeId())) {
			throw new CustomException(PROFILE_NOT_REGISTERED);
		}

		UserType userType = userTypeRepository.findById(user.getUserTypeId())
			.orElseThrow(() -> new CustomException(USER_TYPE_NOT_FOUND));

		LocalDate date = LocalDate.now(clock);

		List<FestivalWithSido> festivals = festivalRepository.findRecommendFestivals(user.getUserTypeId(), size, date);
		List<FestivalInfo> festivalInfos = getFestivalAndSidoWithThumbnailImage(festivals);

		return RecommendFestivalResponse.builder()
			.festivals(festivalInfos)
			.userType(UserTypeResponse.of(userType))
			.build();

	}

	@Transactional
	public FestivalModificationResponse createFestivalRequest(User user, Long festivalId,
		CreateFestivalModificationRequest request) {
		validateUserId(user.getId());
		validateFestival(festivalId);

		FestivalModificationRequest festivalModificationRequest = FestivalModificationRequest.builder()
			.festivalId(festivalId)
			.userId(user.getId())
			.content(request.getContent())
			.isPending(true)
			.build();

		festivalModificationRequestRepository.save(festivalModificationRequest);

		return FestivalModificationResponse.builder()
			.festivalId(festivalId)
			.requestId(festivalModificationRequest.getId())
			.isPending(true)
			.createdAt(festivalModificationRequest.getCreatedAt())
			.build();
	}

	public List<Long> getAllFestivalIds() {
		return festivalRepository.findAllFestivalIds();
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

	private void saveFestivalMood(List<Long> moodIds, Festival festival) {
		festivalMoodRepository.saveAll(moodIds.stream()
			.map(moodId -> FestivalMood.of(festival.getId(), moodId))
			.toList());
	}

	private void saveFestivalCategory(List<Long> categoryIds, Festival festival) {
		festivalCategoryRepository.saveAll(categoryIds.stream()
			.map(categoryId -> FestivalCategory.of(festival.getId(), categoryId))
			.toList());
	}

	private void createFestivalImages(List<MultipartFile> files, Festival festival) {
		if (isNull(files)) {
			return;
		}

		files.forEach(file -> {
			try {
				String imageUrl = fileUtils.uploadImage(file, FESTIVAL_DIR_NAME);
				festivalImageRepository.save(FestivalImage.of(festival.getId(), imageUrl));
			} catch (IOException e) {
				log.error("파일 업로드 실패");
				e.printStackTrace();
			}
		});
	}

	private void saveTopUserTypesForFestival(FestivalCreateRequest request, Festival festival) {
		List<UserType> userTypes = userTypeService.getTopNUserTypes(request.getCategoryIds(),
			request.getMoodIds(), 2);

		festivalUserTypeRepository.saveAll(userTypes.stream()
			.map(userType -> userType.toFestivalUserType(festival))
			.toList());
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

	private void validateFestival(Long festivalId) {
		Festival festival = festivalRepository.findById(festivalId)
			.orElseThrow(() -> new CustomException(FESTIVAL_NOT_FOUND));

		if (festival.isPending()) {
			throw new CustomException(FESTIVAL_IS_PENDING);
		}
	}

	private void validateUserId(Long userId) {
		if (isNull(userId)) {
			throw new CustomException(UNAUTHENTICATED_USER);
		}

		if (!userRepository.existsById(userId)) {
			throw new CustomException(USER_NOT_FOUND);
		}
	}

	private void validateFileCount(List<MultipartFile> files) {
		if (nonNull(files) && files.size() > 3) {
			throw new CustomException(FESTIVAL_IMAGE_EXCEEDED);
		}
	}

	private void validateFileExtension(List<MultipartFile> files) {
		if (nonNull(files)) {
			files.forEach(fileUtils::validateImageExtension);
		}
	}


}
