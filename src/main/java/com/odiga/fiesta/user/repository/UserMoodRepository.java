package com.odiga.fiesta.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.user.domain.mapping.UserMood;

public interface UserMoodRepository extends JpaRepository<UserMood, Long> {
}
