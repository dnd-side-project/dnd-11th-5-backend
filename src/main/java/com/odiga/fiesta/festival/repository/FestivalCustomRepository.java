package com.odiga.fiesta.festival.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.dto.projection.FestivalDetailData;
import com.odiga.fiesta.festival.dto.projection.FestivalWithBookmarkAndSido;
import com.odiga.fiesta.festival.dto.projection.FestivalWithSido;
import com.odiga.fiesta.festival.dto.request.FestivalFilterCondition;

public interface FestivalCustomRepository {

	List<Festival> findFestivalsWithinDateRange(LocalDate startDate, LocalDate endDate);

	Page<FestivalWithBookmarkAndSido> findFestivalsInDate(LocalDate date, Pageable pageable, Long userId);

	Page<FestivalWithBookmarkAndSido> findFestivalsByFiltersAndSort(Long userId,
		FestivalFilterCondition festivalFilterCondition, Double latitude, Double longitude,
		LocalDate date, Pageable pageable);

	Page<FestivalWithSido> findFestivalsAndSidoWithinDateRange(LocalDate startDate, LocalDate endDate,
		Pageable pageable);

	Page<FestivalWithBookmarkAndSido> findFestivalsByQuery(Long userId, String query, Pageable pageable);

	Optional<FestivalDetailData> findFestivalDetail(Long userId, Long festivalId);

	Page<FestivalWithSido> findMostLikeFestival(Pageable pageable);
}
