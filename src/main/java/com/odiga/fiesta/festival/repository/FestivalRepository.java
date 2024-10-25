package com.odiga.fiesta.festival.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.odiga.fiesta.festival.domain.Festival;

public interface FestivalRepository extends JpaRepository<Festival, Long>
	, FestivalCustomRepository {
	boolean existsByUserId(long userId);

	@Query("SELECT f.id FROM Festival f")
	List<Long> findAllFestivalIds();
}
