package com.odiga.fiesta.log.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LogCreateRequest {

	@NotBlank
	private String title;

	@NotBlank
	private String sido;

	@NotBlank
	private String sigungu;

	@NotBlank
	private String address;

	@NotBlank
	@Size(max = 300)
	private String content;

	@Size(min = 1, max = 2, message = "키워드는 1개에서 2개 사이어야 합니다.")
	private List<Long> keywordIds;
}
