package com.odiga.fiesta.festival.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.festival.domain.Companion;

public interface CompanionRepository extends JpaRepository<Companion, Long> {
}
