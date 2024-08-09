package com.odiga.fiesta.log.dto.response;

import static lombok.AccessLevel.*;

import com.odiga.fiesta.log.domain.LogImage;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor(access = PRIVATE)
public class LogImageResponse {

	private final Long imageId;
	private final String imageUrl;

	public static LogImageResponse of(LogImage logImage) {
		return LogImageResponse.builder()
			.imageId(logImage.getId())
			.imageUrl(logImage.getImageUrl())
			.build();
	}
}
