package com.odiga.fiesta.log.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odiga.fiesta.keyword.domain.Keyword;
import com.odiga.fiesta.keyword.repository.KeywordRepository;
import com.odiga.fiesta.log.dto.response.LogKeywordResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LogService {

	private KeywordRepository keywordRepository;

	public List<LogKeywordResponse> getAllLogKeywords() {
		final List<Keyword> keywords = keywordRepository.findAll();
		return keywords.stream()
			.map(LogKeywordResponse::of)
			.toList();
	}

}
