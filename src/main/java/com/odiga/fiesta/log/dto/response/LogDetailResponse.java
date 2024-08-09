package com.odiga.fiesta.log.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LogDetailResponse {

	private Long logId;
	private String title;
	private LocalDateTime date; // 페스티벌에 다녀온 일자
	private String address;
	private Integer rating;
	private String content;
	private List<LogKeywordResponse> keywords;
	private List<LogImageResponse> images;
}
