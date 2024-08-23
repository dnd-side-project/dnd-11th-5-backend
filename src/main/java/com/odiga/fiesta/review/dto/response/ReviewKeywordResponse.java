package com.odiga.fiesta.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewKeywordResponse {

    @Schema(description = "키워드 id", nullable = false, example = "1")
    private Long keywordId;

    @Schema(description = "키워드 URL", nullable = false, example = "✨ 쾌적해요")
    private String keyword;
}
