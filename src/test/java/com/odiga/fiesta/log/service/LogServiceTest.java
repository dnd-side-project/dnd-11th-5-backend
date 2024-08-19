package com.odiga.fiesta.log.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import com.odiga.fiesta.IntegrationTestSupport;
import com.odiga.fiesta.common.error.ErrorCode;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.keyword.domain.Keyword;
import com.odiga.fiesta.keyword.repository.KeywordRepository;
import com.odiga.fiesta.log.domain.Log;
import com.odiga.fiesta.log.domain.LogKeyword;
import com.odiga.fiesta.log.dto.response.LogDetailResponse;
import com.odiga.fiesta.log.dto.response.LogKeywordResponse;
import com.odiga.fiesta.log.repository.LogKeywordRepository;
import com.odiga.fiesta.log.repository.LogRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


class LogServiceTest extends IntegrationTestSupport {


	@InjectMocks
	@Autowired
	private LogService logService;

	@Autowired
	private LogRepository logRepository;

	@Autowired
	private KeywordRepository keywordRepository;

	@Autowired
	private LogKeywordRepository logKeywordRepository;

	@PersistenceContext
	private EntityManager em;

	@DisplayName("모든 활동일지 키워드를 조회한다.")
	@Test
	void getAllLogKeywords() {
		// given
		Keyword keyword1 = createKeyword();
		Keyword keyword2 = createKeyword();
		Keyword keyword3 = createKeyword();
		keywordRepository.saveAll(List.of(keyword1, keyword2, keyword3));

		// when
		List<LogKeywordResponse> keywords = logService.getAllLogKeywords();

		// then
		assertThat(keywords).hasSize(3)
			.usingRecursiveComparison()
			.isEqualTo(Stream.of(keyword1, keyword2, keyword3).map(LogKeywordResponse::of).toList());
	}

	@DisplayName("활동일지 ID를 통해 활동일지를 상세 조회한다.")
	@Test
	void getLogDetail() {
		// given
		Log log1 = createLog();
		Long logId = logRepository.save(log1).getId();

		Keyword keyword1 = createKeyword();
		Keyword keyword2 = createKeyword();
		Keyword keyword3 = createKeyword();
		List<Keyword> savedKeywords = keywordRepository.saveAll(List.of(keyword1, keyword2, keyword3));
		List<LogKeyword> logKeywords = savedKeywords.stream().map(keyword ->
				LogKeyword.builder()
					.keywordId(keyword.getId())
					.logId(logId)
					.build())
			.collect(Collectors.toList());

		logKeywordRepository.saveAll(logKeywords);

		Keyword keyword4 = createKeyword();
		Keyword keyword5 = createKeyword();
		keywordRepository.saveAll(List.of(keyword4, keyword5));

		em.flush();
		em.clear();

		LogDetailResponse expected
			= LogDetailResponse.builder()
			.logId(logId)
			.title(log1.getTitle())
			.date(log1.getDate())
			.address(log1.getAddress())
			.content(log1.getContent())
			.keywords(savedKeywords.stream().map(LogKeywordResponse::of).toList())
			.images(List.of())
			.build();

		// when
		LogDetailResponse logDetail = logService.getLogDetail(logId);

		// then
		assertThat(logDetail).usingRecursiveComparison().isEqualTo(expected);
	}

	@DisplayName("존재하지 않는 활동일지의 ID에서는 에러가 발생한다.")
	@Test
	void getLogDetailNotFound() {
		// given

		// when // then
		assertThatThrownBy(() -> logService.getLogDetail(13L))
			.isInstanceOf(CustomException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.LOG_NOT_FOUND);
	}

	private static Keyword createKeyword() {
		return Keyword.builder().keyword("키워드 이름").build();
	}

	private static Log createLog() {
		LocalDateTime date = LocalDateTime.of(2024, 10, 4, 10, 4);

		return Log.builder()
			.userId(1L)
			.address("페스티벌 주소")
			.title("활동일지 제목")
			.content("활동일지 내용")
			.date(date)
			.isPublic(true)
			.sido("부산광역시")
			.sigungu("금정구")
			.build();
	}

}
