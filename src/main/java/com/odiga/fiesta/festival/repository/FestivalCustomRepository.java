package com.odiga.fiesta.festival.repository;

import java.time.LocalDate;
import java.util.List;

import com.odiga.fiesta.festival.domain.Festival;

public interface FestivalCustomRepository {

	List<Festival> findFestivalsWithinDateRange(LocalDate startDate, LocalDate endDate);
}

