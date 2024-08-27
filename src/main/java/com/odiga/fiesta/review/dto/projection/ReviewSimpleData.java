package com.odiga.fiesta.review.dto.projection;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewSimpleData {

	private Long reviewId;
	private Long festivalId;
	private String festivalName;
	private String content;
	private Integer rating;
}
