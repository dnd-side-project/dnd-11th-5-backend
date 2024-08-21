package com.odiga.fiesta.user.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

public class UserRequest {

	@Getter
	@Builder
	public static class createProfileDTO {

		@Schema(description = "카테고리 id", nullable = false, example = "[1, 2]")
		@NotEmpty(message = "카테고리 리스트는 비어 있을 수 없습니다.")
		@Size(min = 2, max = 2, message = "카테고리는 2개여야 합니다.")
		private List<Long> category;

		@Schema(description = "분위기 id", nullable = false, example = "[1, 2, 3]")
		@NotEmpty(message = "분위기 리스트는 비어 있을 수 없습니다.")
		@Size(min = 3, max = 3, message = "분위기는 3개여야 합니다.")
		private List<Long> mood;

		@Schema(description = "동행유형 id", nullable = false, example = "[1, 2]")
		@NotEmpty(message = "동행유형 리스트는 비어 있을 수 없습니다.")
		@Size(min = 1, message = "동행유형은 1개 이상이어야 합니다.")
		private List<Long> companion;

		@Schema(description = "우선순위 id", nullable = false, example = "[1, 2, 3]")
		@NotEmpty(message = "우선순위 리스트는 비어 있을 수 없습니다.")
		@Size(min = 3, max = 3, message = "우선순위는 3개여야 합니다.")
		private List<Long> priority;
	}
}
