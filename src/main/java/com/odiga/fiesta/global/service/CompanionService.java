package com.odiga.fiesta.global.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odiga.fiesta.global.domain.Companion;
import com.odiga.fiesta.festival.dto.response.CompanionResponse;
import com.odiga.fiesta.global.repository.CompanionRepository;

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
