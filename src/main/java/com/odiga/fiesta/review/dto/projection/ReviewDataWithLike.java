package com.odiga.fiesta.review.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ReviewDataWithLike extends ReviewData {

	private Boolean isLiked;
	private Long likeCount;

}
