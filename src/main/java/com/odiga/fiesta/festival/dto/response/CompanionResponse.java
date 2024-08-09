package com.odiga.fiesta.festival.dto.response;

import static lombok.AccessLevel.*;

import com.odiga.fiesta.festival.domain.Companion;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class CompanionResponse {

	private final Long companionId;
	private final String companionType;

	public static CompanionResponse of(final Companion companion) {
		return new CompanionResponse(
			companion.getId(),
			companion.getCompanionType()
		);
	}
}
