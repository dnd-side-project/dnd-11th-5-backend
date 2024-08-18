package com.odiga.fiesta.festival.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.odiga.fiesta.festival.domain.FestivalCategory;

public interface FestivalCategoryRepository extends JpaRepository<FestivalCategory, Long> {

	@Query("SELECT fc.categoryId FROM FestivalCategory fc WHERE fc.festivalId = :festivalId")
	List<Long> findAllCategoryIdByFestivalId(@Param("festivalId") Long festivalId);
}
