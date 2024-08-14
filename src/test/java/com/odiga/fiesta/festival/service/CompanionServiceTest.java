package com.odiga.fiesta.festival.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.odiga.fiesta.MockTestSupport;
import com.odiga.fiesta.companion.domain.Companion;
import com.odiga.fiesta.festival.dto.response.CompanionResponse;
import com.odiga.fiesta.companion.repository.CompanionRepository;

class CompanionServiceTest extends MockTestSupport {

	@InjectMocks
	private CompanionService companionService;

	@Mock
	private CompanionRepository companionRepository;

	@DisplayName("모든 일행 타입을 반환한다.")
	@Test
	void getAllCompanions() {
		// given
		List<Companion> companions = List.of(
			Companion.builder().id(1L).companionType("가족").build(),
			Companion.builder().id(2L).companionType("친구").build()
		);

		given(companionRepository.findAll())
			.willReturn(companions);

		// when
		final List<CompanionResponse> actual = companionService.getAllCompanions();

		// then
		assertThat(actual).usingRecursiveComparison()
			.isEqualTo(companions.stream().map(CompanionResponse::of).toList());
	}
}
