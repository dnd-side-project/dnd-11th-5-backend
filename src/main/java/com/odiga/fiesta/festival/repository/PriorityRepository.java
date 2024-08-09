package com.odiga.fiesta.festival.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.festival.domain.Priority;

public interface PriorityRepository extends JpaRepository<Priority, Long> {
}
