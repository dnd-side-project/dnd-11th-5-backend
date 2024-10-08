package com.odiga.fiesta.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.user.domain.mapping.UserPriority;

public interface UserPriorityRepository extends JpaRepository<UserPriority, Long> {

	void deleteByUserId(Long userId);
}
