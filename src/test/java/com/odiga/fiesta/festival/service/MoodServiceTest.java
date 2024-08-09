package com.odiga.fiesta.festival.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.odiga.fiesta.MockTestSupport;
import com.odiga.fiesta.global.domain.Mood;
import com.odiga.fiesta.festival.dto.response.MoodResponse;
import com.odiga.fiesta.global.repository.MoodRepository;
import com.odiga.fiesta.global.service.MoodService;

class MoodServiceTest extends MockTestSupport {

	@InjectMocks
	private MoodService moodService;

	@Mock
	private MoodRepository moodRepository;

	@DisplayName("모든 분위기 타입을 반환한다.")
	@Test
	void getAllMoods() {
		// given
		List<Mood> moods = List.of(
			Mood.builder().id(1L).mood("낭만적인").build(),
			Mood.builder().id(2L).mood("모험적인").build()
		);

		given(moodRepository.findAll())
			.willReturn(moods);

		// when
		final List<MoodResponse> actual = moodService.getAllMoods();

		// then
		assertThat(actual).usingRecursiveComparison()
			.isEqualTo(moods.stream().map(MoodResponse::of).toList());
	}
}
