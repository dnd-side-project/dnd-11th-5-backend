package com.odiga.fiesta.festival.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odiga.fiesta.festival.dto.response.MoodResponse;
import com.odiga.fiesta.mood.domain.Mood;
import com.odiga.fiesta.mood.repository.MoodRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MoodService {

	private final MoodRepository moodRepository;

	public List<MoodResponse> getAllMoods() {
		final List<Mood> moods = moodRepository.findAll();
		return moods.stream()
			.map(MoodResponse::of)
			.toList();
	}
}
