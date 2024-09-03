package com.odiga.fiesta.festival.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class FestivalModificationResponse {

	private Long festivalId;
	private Long requestId;
	private boolean isPending;
	private LocalDateTime createdAt;

	@Builder
	public FestivalModificationResponse(Long festivalId, Long requestId, boolean isPending, LocalDateTime createdAt) {
		this.festivalId = festivalId;
		this.requestId = requestId;
		this.isPending = isPending;
		this.createdAt = createdAt;
	}
}
