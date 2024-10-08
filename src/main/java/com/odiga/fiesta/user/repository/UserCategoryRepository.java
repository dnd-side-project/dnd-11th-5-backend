package com.odiga.fiesta.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.odiga.fiesta.user.domain.mapping.UserCategory;

public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {

	@Query("SELECT uc.categoryId FROM UserCategory uc WHERE uc.userId = :userId")
	List<Long> findCategoryIdsByUserId(@Param("userId") Long userId);


	void deleteByUserId(Long userId);
}
