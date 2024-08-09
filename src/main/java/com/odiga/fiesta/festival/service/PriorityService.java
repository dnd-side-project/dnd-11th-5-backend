package com.odiga.fiesta.festival.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odiga.fiesta.festival.domain.Priority;
import com.odiga.fiesta.festival.dto.response.PriorityResponse;
import com.odiga.fiesta.festival.repository.PriorityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PriorityService {

	private final PriorityRepository priorityRepository;

	public List<PriorityResponse> getAllPriorities() {
		final List<Priority> priorities = priorityRepository.findAll();
		return priorities.stream()
			.map(PriorityResponse::of)
			.toList();
	}
}
