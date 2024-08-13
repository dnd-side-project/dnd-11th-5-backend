package com.odiga.fiesta.festival.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.festival.domain.Festival;

public interface FestivalRepository extends JpaRepository<Festival, Long> {
}
