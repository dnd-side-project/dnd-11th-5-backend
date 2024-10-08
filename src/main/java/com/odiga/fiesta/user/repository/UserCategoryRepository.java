package com.odiga.fiesta.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.user.domain.mapping.UserCategory;

public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {

	void deleteByUserId(Long userId);
}
