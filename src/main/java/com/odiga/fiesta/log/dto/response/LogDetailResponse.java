package com.odiga.fiesta.log.dto.response;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.*;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LogDetailResponse {

	private Long logId;
	private String title;

	@JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
	private LocalDateTime date; // 페스티벌에 다녀온 일자
	private String address;
	private Double rating;
	private String content;
	private List<LogKeywordResponse> keywords;
	private List<LogImageResponse> images;
}
