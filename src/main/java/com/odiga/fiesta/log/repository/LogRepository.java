package com.odiga.fiesta.log.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.log.domain.Log;

public interface LogRepository extends JpaRepository<Log, Long> {
}
