package com.odiga.fiesta.review.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TopReviewResponse {

    @Schema(description = "리뷰 id", nullable = false, example = "1")
    private Long reviewId;

    @Schema(description = "페스티벌 id", nullable = false, example = "1")
    private Long festivalId;

    @Schema(description = "페스티벌 이름", nullable = false, example = "2024 입크 IBK 페스티벌")
    private String festivalName;

    @Schema(description = "리뷰 내용", nullable = false, example = "완전 더워요..")
    private String content;

    @Schema(description = "별점", nullable = false, example = "5")
    private double rating;

    private ReviewImageResponse images;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ReviewKeywordResponse> keywords;
}
