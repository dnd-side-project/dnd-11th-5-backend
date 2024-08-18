package com.odiga.fiesta.festival.dto.response;

import com.odiga.fiesta.festival.domain.FestivalImage;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FestivalImageResponse {
	private Long imageId;
	private String imageUrl;

	public static FestivalImageResponse of(FestivalImage image) {
		return FestivalImageResponse.builder()
			.imageId(image.getId())
			.imageUrl(image.getImageUrl())
			.build();
	}
}
