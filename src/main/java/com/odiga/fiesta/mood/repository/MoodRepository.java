package com.odiga.fiesta.mood.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.mood.domain.Mood;

public interface MoodRepository extends JpaRepository<Mood, Long> {
}
