package com.odiga.fiesta.badge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.odiga.fiesta.badge.domain.UserBadge;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

	@Query("SELECT ub.badgeId FROM UserBadge ub WHERE ub.userId= :userId AND ub.badgeId IN :badgeIds")
	List<Long> findBadgeIdByUserIdAndBadgeIdIn(@Param("userId") Long userId, @Param("badgeIds") List<Long> badgeIds);

	Boolean existsByBadgeId(Long badgeId);
}
