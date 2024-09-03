package com.odiga.fiesta.festival.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FestivalModificationRequest {

	private String content;

	@Builder
	public FestivalModificationRequest(String content) {
		this.content = content;
	}
}
