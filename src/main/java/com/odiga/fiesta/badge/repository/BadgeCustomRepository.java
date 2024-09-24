package com.odiga.fiesta.badge.repository;

import java.util.List;

import com.odiga.fiesta.user.dto.response.UserBadgeResponse;

public interface BadgeCustomRepository {

	List<UserBadgeResponse> findUserBadges(Long userId);
}
