package com.odiga.fiesta.review.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewReportRequest {

	@NotBlank
	@Size(max = 500)
	private String description;
}
