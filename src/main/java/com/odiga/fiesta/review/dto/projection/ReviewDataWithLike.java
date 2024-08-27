package com.odiga.fiesta.review.dto.projection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@SuperBuilder
public class ReviewDataWithLike extends ReviewData {

	private Boolean isLiked;
	private Long likeCount;
}
