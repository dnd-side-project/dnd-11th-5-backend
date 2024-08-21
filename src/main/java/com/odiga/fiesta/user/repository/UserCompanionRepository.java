package com.odiga.fiesta.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.user.domain.mapping.UserCompanion;

public interface UserCompanionRepository extends JpaRepository<UserCompanion, Long> {
}
