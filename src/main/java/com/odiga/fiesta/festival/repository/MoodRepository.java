package com.odiga.fiesta.festival.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.festival.domain.Mood;

public interface MoodRepository extends JpaRepository<Mood, Long> {
}
