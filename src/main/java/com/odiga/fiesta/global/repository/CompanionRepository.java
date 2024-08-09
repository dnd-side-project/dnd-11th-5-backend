package com.odiga.fiesta.global.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.global.domain.Companion;

public interface CompanionRepository extends JpaRepository<Companion, Long> {
}
