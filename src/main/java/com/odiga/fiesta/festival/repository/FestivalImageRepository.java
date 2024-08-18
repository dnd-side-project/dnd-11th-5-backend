package com.odiga.fiesta.festival.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.odiga.fiesta.festival.domain.FestivalImage;

public interface FestivalImageRepository extends JpaRepository<FestivalImage, Long> {

	@Query(value = "SELECT fi.image_url FROM festival_image fi WHERE fi.festival_id = :festivalId LIMIT 1", nativeQuery = true)
	String findFirstImageUrlByFestivalId(@Param("festivalId") Long festivalId);

	List<FestivalImage> findAllByFestivalId(Long festivalId);
}
