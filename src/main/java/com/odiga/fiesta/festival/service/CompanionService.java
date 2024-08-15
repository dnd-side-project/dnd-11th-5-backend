package com.odiga.fiesta.festival.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odiga.fiesta.companion.domain.Companion;
import com.odiga.fiesta.companion.repository.CompanionRepository;
import com.odiga.fiesta.festival.dto.response.CompanionResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanionService {

	private final CompanionRepository companionRepository;

	public List<CompanionResponse> getAllCompanions() {
		final List<Companion> companions = companionRepository.findAll();
		return companions.stream()
			.map(CompanionResponse::of)
			.toList();
	}
}
