package com.odiga.fiesta.log.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.log.domain.LogImage;

public interface LogImageRepository extends JpaRepository<LogImage, Long> {
	List<LogImage> findAllByLogId(Long logId);
}
