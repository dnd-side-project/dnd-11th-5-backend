package com.odiga.fiesta.review.dto.request;

import java.util.Collections;
import java.util.List;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewUpdateRequest {

	@NotNull
	private Double rating;

	@NotNull
	private List<Long> keywordIds;

	@NotNull
	private List<Long> deletedImages;

	@NotBlank
	private String content;

	@AssertTrue(message = "평점은 0.5 단위로 입력해야 합니다.")
	private boolean isRatingValid() {
		return rating != null && (rating * 10) % 5 == 0;
	}

	@Builder
	public ReviewUpdateRequest(Double rating, List<Long> keywordIds, List<Long> deletedImages, String content) {
		this.rating = rating;
		this.keywordIds = removeDuplicates(keywordIds);
		this.deletedImages = removeDuplicates(deletedImages);
		this.content = content;
	}

	private List<Long> removeDuplicates(List<Long> list) {
		return list == null ? Collections.emptyList() : list.stream().distinct().toList();
	}
}
