package com.odiga.fiesta.festival.dto.response;

import static lombok.AccessLevel.*;

import com.odiga.fiesta.global.domain.Mood;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class MoodResponse {

	private final Long moodId;
	private final String mood;

	public static MoodResponse of(final Mood mood) {
		return new MoodResponse(
			mood.getId(),
			mood.getMood()
		);
	}
}
