package com.odiga.fiesta.review.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewReportResponse {

	private Long reportId;
	private Long reviewId;
	private Boolean isPending;
	private LocalDateTime createdAt;
}
