package com.odiga.fiesta.festival.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.festival.domain.FestivalBookmark;

public interface FestivalBookmarkRepository extends JpaRepository<FestivalBookmark, Long> {

	Long countByFestivalId(Long festivalId);

	Optional<FestivalBookmark> findByUserIdAndFestivalId(Long userId, Long festivalId);
}
