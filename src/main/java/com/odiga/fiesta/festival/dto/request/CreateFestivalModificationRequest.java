package com.odiga.fiesta.festival.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateFestivalModificationRequest {

	private String content;

	@Builder
	public CreateFestivalModificationRequest(String content) {
		this.content = content;
	}
}
