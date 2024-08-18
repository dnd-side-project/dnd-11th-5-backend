package com.odiga.fiesta.user.repository;

import com.odiga.fiesta.user.domain.mapping.UserCompanion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCompanionRepository extends JpaRepository<UserCompanion, Long> {
}
