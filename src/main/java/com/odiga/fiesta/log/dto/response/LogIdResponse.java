package com.odiga.fiesta.log.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LogIdResponse {
	private Long logId;

	public static LogIdResponse of(Long id) {
		return LogIdResponse.builder()
			.logId(id)
			.build();
	}
}
