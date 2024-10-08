package com.odiga.fiesta.festival.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.dto.projection.FestivalDetailData;
import com.odiga.fiesta.festival.dto.projection.FestivalWithBookmarkAndSido;
import com.odiga.fiesta.festival.dto.projection.FestivalWithSido;
import com.odiga.fiesta.festival.dto.request.FestivalFilterCondition;
import com.odiga.fiesta.festival.dto.response.FestivalAndLocation;
import com.odiga.fiesta.festival.dto.response.FestivalInfoWithBookmark;

public interface FestivalCustomRepository {

	List<Festival> findFestivalsWithinDateRange(LocalDate startDate, LocalDate endDate);

	Page<FestivalWithBookmarkAndSido> findFestivalsInDate(LocalDate date, Pageable pageable, Long userId);

	Page<FestivalWithBookmarkAndSido> findFestivalsByFiltersAndSort(Long userId,
		FestivalFilterCondition festivalFilterCondition, Double latitude, Double longitude,
		LocalDate date, Pageable pageable);

	Page<FestivalWithSido> findFestivalsAndSidoWithinDateRange(LocalDate startDate, LocalDate endDate,
		Pageable pageable);

	Page<FestivalWithBookmarkAndSido> findFestivalsByQuery(Long userId, String query, Pageable pageable);

	Page<FestivalWithSido> findMostLikeFestival(Pageable pageable, LocalDate date);

	Optional<FestivalDetailData> findFestivalDetail(Long userId, Long festivalId);

	Page<FestivalAndLocation> findUpcomingFestivalAndLocation(Long userId, LocalDate date, Pageable pageable);

	List<FestivalWithSido> findRecommendFestivals(Long userTypeId, Long size, LocalDate date);

	Map<Long, String> findThumbnailImageByFestivalId(List<Long> festivalIds);

	Page<FestivalWithBookmarkAndSido> findBookmarkedFestivals(Long userId, Pageable pageable);
}
