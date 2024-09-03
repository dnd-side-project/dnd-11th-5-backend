package com.odiga.fiesta.festival.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.festival.domain.FestivalModificationRequest;

public interface FestivalModificationRequestRepository extends JpaRepository<FestivalModificationRequest, Long> {
}
