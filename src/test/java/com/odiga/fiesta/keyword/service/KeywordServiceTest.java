package com.odiga.fiesta.keyword.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.odiga.fiesta.MockTestSupport;
import com.odiga.fiesta.keyword.domain.Keyword;
import com.odiga.fiesta.keyword.dto.KeywordResponse;
import com.odiga.fiesta.keyword.repository.KeywordRepository;

class KeywordServiceTest extends MockTestSupport {

	@InjectMocks
	private KeywordService keywordService;

	@Mock
	private KeywordRepository keywordRepository;

	@DisplayName("키워드 목록을 조회한다.")
	@Test
	void getAllKeywords() {
		// given
		List<Keyword> keywords = List.of(
			Keyword.of(1L, "✨ 쾌적해요"),
			Keyword.of(2L, "\uD83D\uDC40 볼거리가 많아요")
		);

		given(keywordRepository.findAll())
			.willReturn(keywords);

		// when
		final List<KeywordResponse> actual = keywordService.getAllKeywords();

		// then
		assertThat(actual).usingRecursiveComparison()
			.isEqualTo(keywords.stream().map(KeywordResponse::of).toList());
	}
}
