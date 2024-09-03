package com.odiga.fiesta.festival.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateFestivalModificationRequest {

	@NotBlank
	private String content;

	@Builder
	public CreateFestivalModificationRequest(String content) {
		this.content = content;
	}
}
