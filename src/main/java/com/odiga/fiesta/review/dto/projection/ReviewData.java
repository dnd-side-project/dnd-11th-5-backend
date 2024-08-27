package com.odiga.fiesta.review.dto.projection;

import java.time.LocalDateTime;

import com.odiga.fiesta.review.dto.response.ReviewUserInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class ReviewData {

	private Long reviewId;
	private Long festivalId;
	private ReviewUserInfo user;
	private String content;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	// private Boolean isEdited;
	private Integer rating;
}
