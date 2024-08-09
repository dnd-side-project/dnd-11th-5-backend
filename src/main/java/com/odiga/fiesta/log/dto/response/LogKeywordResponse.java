package com.odiga.fiesta.log.dto.response;

import static lombok.AccessLevel.*;

import com.odiga.fiesta.keyword.domain.Keyword;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class LogKeywordResponse {

	private final Long keywordId;
	private final String keyword;

	public static LogKeywordResponse of(final Keyword keyword) {
		return new LogKeywordResponse(
			keyword.getId(),
			keyword.getKeyword()
		);
	}
}
