package com.odiga.fiesta.log.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odiga.fiesta.common.error.ErrorCode;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.keyword.domain.Keyword;
import com.odiga.fiesta.keyword.repository.KeywordRepository;
import com.odiga.fiesta.log.domain.Log;
import com.odiga.fiesta.log.domain.LogKeyword;
import com.odiga.fiesta.log.dto.response.LogDetailResponse;
import com.odiga.fiesta.log.dto.response.LogImageResponse;
import com.odiga.fiesta.log.dto.response.LogKeywordResponse;
import com.odiga.fiesta.log.repository.LogImageRepository;
import com.odiga.fiesta.log.repository.LogKeywordRepository;
import com.odiga.fiesta.log.repository.LogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LogService {

	private final KeywordRepository keywordRepository;
	private final LogImageRepository logImageRepository;
	private final LogKeywordRepository logKeywordRepository;
	private final LogRepository logRepository;

	public List<LogKeywordResponse> getAllLogKeywords() {
		List<Keyword> keywords = keywordRepository.findAll();
		return keywords.stream()
			.map(LogKeywordResponse::of)
			.toList();
	}

	public LogDetailResponse getLogDetail(Long logId) {
		Log log = logRepository.findById(logId)
			.orElseThrow(() -> new CustomException(ErrorCode.LOG_NOT_FOUND));

		List<LogImageResponse> logImages = logImageRepository.findAllByLogId(logId)
			.stream()
			.map(LogImageResponse::of)
			.toList();

		List<Long> keywordIds = logKeywordRepository.findAllByLogId(logId)
			.stream()
			.map(LogKeyword::getKeywordId)
			.toList();

		List<LogKeywordResponse> keywords = keywordRepository.findAllByIdIn(keywordIds).stream()
			.map(LogKeywordResponse::of)
			.toList();

		return LogDetailResponse.builder()
			.logId(log.getId())
			.title(log.getTitle())
			.date(log.getDate())
			.address(log.getAddress())
			.rating((double) log.getRating() / 2)
			.content(log.getContent())
			.keywords(keywords)
			.images(logImages)
			.build();
	}
}
