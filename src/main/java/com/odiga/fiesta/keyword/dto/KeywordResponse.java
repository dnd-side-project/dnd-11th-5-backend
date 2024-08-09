package com.odiga.fiesta.keyword.dto;

import static lombok.AccessLevel.*;

import com.odiga.fiesta.keyword.domain.Keyword;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class KeywordResponse {

	private final Long keywordId;
	private final String keyword;

	public static KeywordResponse of(final Keyword keyword) {
		return new KeywordResponse(
			keyword.getId(),
			keyword.getKeyword()
		);
	}
}
