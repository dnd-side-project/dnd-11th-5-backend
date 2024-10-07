package com.odiga.fiesta.user.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserOnboardingResponse {
	private List<Long> categoryIds;
	private List<Long> moodIds;
	private List<Long> companionIds;
	private List<Long> priorityIds;

	@Builder
	private UserOnboardingResponse(List<Long> categoryIds, List<Long> moodIds, List<Long> companionIds,
		List<Long> priorityIds) {
		this.categoryIds = categoryIds;
		this.moodIds = moodIds;
		this.companionIds = companionIds;
		this.priorityIds = priorityIds;
	}
}
