package com.odiga.fiesta.log.dto.response;

import static lombok.AccessLevel.*;

import com.odiga.fiesta.keyword.domain.Keyword;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor(access = PRIVATE)
public class LogKeywordResponse {

	private final Long keywordId;
	private final String keyword;

	public static LogKeywordResponse of(final Keyword keyword) {
		return LogKeywordResponse.builder()
			.keywordId(keyword.getId())
			.keyword(keyword.getKeyword())
			.build();
	}
}
