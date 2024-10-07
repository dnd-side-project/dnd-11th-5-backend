package com.odiga.fiesta.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.odiga.fiesta.user.domain.mapping.UserMood;

public interface UserMoodRepository extends JpaRepository<UserMood, Long> {


	@Query("SELECT um.moodId FROM UserMood um WHERE um.userId = :userId")
	List<Long> findMoodIdsByUserId(@Param("userId") Long userId);
}
