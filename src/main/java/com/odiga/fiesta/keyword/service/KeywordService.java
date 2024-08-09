package com.odiga.fiesta.keyword.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odiga.fiesta.keyword.domain.Keyword;
import com.odiga.fiesta.keyword.dto.KeywordResponse;
import com.odiga.fiesta.keyword.repository.KeywordRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KeywordService {

	private KeywordRepository keywordRepository;

	public List<KeywordResponse> getAllKeywords(){
		final List<Keyword> keywords = keywordRepository.findAll();
		return keywords.stream()
			.map(KeywordResponse::of)
			.toList();
	}
}
