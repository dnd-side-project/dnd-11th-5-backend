package com.odiga.fiesta.log.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public class LogSimpleResponse {

	private Long logId;
	private String title;
	private LocalDateTime date; // 페스티벌에 다녀온 일자
	private String thumbnailImage;
}
