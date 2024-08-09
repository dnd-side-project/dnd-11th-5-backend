package com.odiga.fiesta.global.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.global.domain.Priority;

public interface PriorityRepository extends JpaRepository<Priority, Long> {
}
