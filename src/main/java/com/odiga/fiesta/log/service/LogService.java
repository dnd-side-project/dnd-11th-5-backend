package com.odiga.fiesta.log.service;

import static com.odiga.fiesta.common.error.ErrorCode.*;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.common.util.FileUtils;
import com.odiga.fiesta.keyword.domain.Keyword;
import com.odiga.fiesta.keyword.repository.KeywordRepository;
import com.odiga.fiesta.log.domain.Log;
import com.odiga.fiesta.log.domain.LogImage;
import com.odiga.fiesta.log.domain.LogKeyword;
import com.odiga.fiesta.log.dto.request.LogCreateRequest;
import com.odiga.fiesta.log.dto.response.LogDetailResponse;
import com.odiga.fiesta.log.dto.response.LogIdResponse;
import com.odiga.fiesta.log.dto.response.LogImageResponse;
import com.odiga.fiesta.log.dto.response.LogKeywordResponse;
import com.odiga.fiesta.log.repository.LogImageRepository;
import com.odiga.fiesta.log.repository.LogKeywordRepository;
import com.odiga.fiesta.log.repository.LogRepository;
import com.odiga.fiesta.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LogService {

	private static final String IMAGE_DIR_NAME = "log";

	private final KeywordRepository keywordRepository;
	private final LogImageRepository logImageRepository;
	private final LogKeywordRepository logKeywordRepository;
	private final LogRepository logRepository;
	private final UserRepository userRepository;

	private final FileUtils fileUtils;

	public List<LogKeywordResponse> getAllLogKeywords() {
		List<Keyword> keywords = keywordRepository.findAll();
		return keywords.stream()
			.map(LogKeywordResponse::of)
			.toList();
	}

	public LogDetailResponse getLogDetail(Long logId) {
		Log log = logRepository.findById(logId)
			.orElseThrow(() -> new CustomException(LOG_NOT_FOUND));

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
			.content(log.getContent())
			.keywords(keywords)
			.images(logImages)
			.build();
	}

	@Transactional
	public LogIdResponse createLog(Long userId, LogCreateRequest request, List<MultipartFile> files) {
		validateUser(userId);

		Log log = Log.builder()
			.title(request.getTitle())
			.sido(request.getSido())
			.sigungu(request.getSigungu())
			.address(request.getAddress())
			.content(request.getContent())
			.userId(userId)
			.build();

		Log savedLog = logRepository.save(log);

		if (files != null) {
			createLogImages(savedLog.getId(), files);
		}

		return LogIdResponse.of(savedLog.getId());
	}

	@Transactional
	public void createLogImages(Long logId, List<MultipartFile> files) {
		files.forEach(file -> {
			try {
				String imageUrl = fileUtils.upload(file, IMAGE_DIR_NAME);
				logImageRepository.save(LogImage.builder()
					.logId(logId)
					.imageUrl(imageUrl)
					.build());
			} catch (IOException e) {
				throw new CustomException(UPLOAD_FAIL);
			}
		});
	}

	private void validateUser(Long id) {
		userRepository.findById(id)
			.orElseThrow(() -> new CustomException(USER_NOT_FOUND));
	}

}
