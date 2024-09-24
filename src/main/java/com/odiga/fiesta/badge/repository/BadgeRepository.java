package com.odiga.fiesta.badge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.odiga.fiesta.badge.domain.Badge;
import com.odiga.fiesta.badge.domain.BadgeType;

public interface BadgeRepository extends JpaRepository<Badge, Long>, BadgeCustomRepository {

	@Query("SELECT b.id FROM Badge b WHERE b.type = :type")
	List<Long> findIdsByType(@Param("type") BadgeType type);
}
