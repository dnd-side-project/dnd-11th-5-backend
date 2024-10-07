package com.odiga.fiesta.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.odiga.fiesta.user.domain.mapping.UserCompanion;

public interface UserCompanionRepository extends JpaRepository<UserCompanion, Long> {

	@Query("SELECT uc.companionId FROM UserCompanion uc WHERE uc.userId = :userId")
	List<Long> findCompanionIdsByUserId(@Param("userId") Long userId);
}
