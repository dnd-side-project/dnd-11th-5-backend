package com.odiga.fiesta.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewImageResponse {

	@Schema(description = "이미지 id", example = "1")
	private Long imageId;

	@Schema(description = "이미지 URL", example = "...")
	private String imageUrl;
}
