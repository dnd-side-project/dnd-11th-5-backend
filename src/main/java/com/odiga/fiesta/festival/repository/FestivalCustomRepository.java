package com.odiga.fiesta.festival.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.dto.projection.FestivalWithBookmarkAndSido;

public interface FestivalCustomRepository {

	List<Festival> findFestivalsWithinDateRange(LocalDate startDate, LocalDate endDate);
	Page<FestivalWithBookmarkAndSido> findFestivalsInDate(LocalDate date, Pageable pageable, Long userId);
}

