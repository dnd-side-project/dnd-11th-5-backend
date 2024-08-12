package com.odiga.fiesta.keyword.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.keyword.domain.Keyword;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

	List<Keyword> findAllByIdIn(List<Long> keywordIds);
}
