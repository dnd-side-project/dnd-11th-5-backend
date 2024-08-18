package com.odiga.fiesta.festival.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.odiga.fiesta.festival.domain.FestivalImage;

public interface FestivalImageRepository extends JpaRepository<FestivalImage, Long> {

	@Query("SELECT fi.imageUrl FROM FestivalImage fi WHERE fi.festivalId = :festivalId")
	String findImageUrlByFestivalId(@Param("festivalId") Long festivalId);

	List<FestivalImage> findAllByFestivalId(Long festivalId);
}
