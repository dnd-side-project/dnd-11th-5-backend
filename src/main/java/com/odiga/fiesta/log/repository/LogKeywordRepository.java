package com.odiga.fiesta.log.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.log.domain.LogKeyword;

public interface LogKeywordRepository extends JpaRepository<LogKeyword, Long> {
	List<LogKeyword> findAllByLogId(Long logId);
}
