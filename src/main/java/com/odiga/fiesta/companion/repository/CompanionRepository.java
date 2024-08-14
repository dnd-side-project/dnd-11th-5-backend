package com.odiga.fiesta.companion.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.companion.domain.Companion;

public interface CompanionRepository extends JpaRepository<Companion, Long> {
}
