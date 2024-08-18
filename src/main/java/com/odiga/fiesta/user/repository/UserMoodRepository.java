package com.odiga.fiesta.user.repository;

import com.odiga.fiesta.user.domain.mapping.UserMood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMoodRepository extends JpaRepository<UserMood, Long> {
}
