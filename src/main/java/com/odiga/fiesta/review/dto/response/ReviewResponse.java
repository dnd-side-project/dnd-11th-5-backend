package com.odiga.fiesta.review.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    @Schema(description = "리뷰 id", nullable = false, example = "1")
    private Long reviewId;

    @Schema(description = "작성자 닉네임", nullable = false, example = "하이하이")
    private String nickname;

    @Schema(description = "리뷰 내용", nullable = false, example = "완전 더워요..")
    private String content;

    @Schema(description = "작성일", nullable = false, example = "2024-08-01")
    private LocalDate date;

    @Schema(description = "별점", nullable = false, example = "5")
    private double rating;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ReviewImageResponse> images;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ReviewKeywordResponse> keywords;

    @Schema(description = "좋아요 여부", nullable = false, example = "false")
    private boolean isLiked;

    @Schema(description = "좋아요 수", nullable = false, example = "12")
    private int likes;

    @Schema(description = "작성 여부", nullable = false, example = "false")
    private boolean isMyReview;
}
