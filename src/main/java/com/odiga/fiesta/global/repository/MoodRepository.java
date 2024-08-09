package com.odiga.fiesta.global.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.global.domain.Mood;

public interface MoodRepository extends JpaRepository<Mood, Long> {
}
