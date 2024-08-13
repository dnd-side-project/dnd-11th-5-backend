package com.odiga.fiesta.priority.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.priority.domain.Priority;

public interface PriorityRepository extends JpaRepository<Priority, Long> {
}
