package com.odiga.fiesta.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBadgeResponse {

	private Long badgeId;
	private String badgeName;
	private String description;
	private String imageUrl;
	private Boolean isAcquired;
}
