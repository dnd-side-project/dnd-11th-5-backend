package com.odiga.fiesta.festival.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.odiga.fiesta.MockTestSupport;
import com.odiga.fiesta.festival.dto.response.PriorityResponse;
import com.odiga.fiesta.priority.domain.Priority;
import com.odiga.fiesta.priority.repository.PriorityRepository;

class PriorityServiceTest extends MockTestSupport {

	@InjectMocks
	private PriorityService priorityService;

	@Mock
	private PriorityRepository priorityRepository;

	@DisplayName("모든 페스티벌 우선순위 항목을 반환한다.")
	@Test
	void getAllPriorities() {
		// given
		List<Priority> priorities = List.of(
			Priority.builder().id(1L).priority("주제 관심사 일치").build(),
			Priority.builder().id(2L).priority("위치").build()
		);

		given(priorityRepository.findAll())
			.willReturn(priorities);

		// when
		final List<PriorityResponse> actual = priorityService.getAllPriorities();

		// then
		assertThat(actual).usingRecursiveComparison()
			.isEqualTo(priorities.stream().map(PriorityResponse::of).toList());
	}
}
