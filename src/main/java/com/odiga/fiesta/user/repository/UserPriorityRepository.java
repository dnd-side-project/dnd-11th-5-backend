package com.odiga.fiesta.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.odiga.fiesta.user.domain.mapping.UserPriority;

public interface UserPriorityRepository extends JpaRepository<UserPriority, Long> {

	@Query("SELECT up.priorityId FROM UserPriority up WHERE up.userId = :userId")
	List<Long> findPriorityIdsByUserId(@Param("userId") Long userId);

	void deleteByUserId(Long userId);
}
