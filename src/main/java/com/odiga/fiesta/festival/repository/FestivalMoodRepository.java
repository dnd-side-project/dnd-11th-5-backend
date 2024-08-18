package com.odiga.fiesta.festival.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.odiga.fiesta.festival.domain.FestivalMood;

public interface FestivalMoodRepository extends JpaRepository<FestivalMood, Long> {

	@Query("SELECT fm.moodId FROM FestivalMood fm WHERE fm.festivalId = :festivalId")
	List<Long> findAllMoodIdByFestivalId(@Param("festivalId") Long festivalId);
}
