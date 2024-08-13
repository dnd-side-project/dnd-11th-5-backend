package com.odiga.fiesta.festival.dto.response;

import static lombok.AccessLevel.*;

import com.odiga.fiesta.priority.domain.Priority;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class PriorityResponse {

	private final Long priorityId;
	private final String priority;

	public static PriorityResponse of(final Priority priority) {
		return new PriorityResponse(
			priority.getId(),
			priority.getPriority()
		);
	}
}
