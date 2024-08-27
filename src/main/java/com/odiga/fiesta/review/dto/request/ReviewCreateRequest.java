package com.odiga.fiesta.review.dto.request;

import java.util.List;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewCreateRequest {

	@NotNull
	private Long festivalId;

	@NotNull
	@Min(0)
	@Max(5)
	private Double rating;

	@NotEmpty
	private List<@NotNull Long> keywordIds;

	@NotBlank
	private String content;

	@AssertTrue(message = "평점은 0.5 단위로 입력해야 합니다.")
	private boolean isRatingValid() {
		return rating != null && (rating * 10) % 5 == 0;
	}
}
