package com.odiga.fiesta.review.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class ReviewResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class getReviewsDTO {

        @Schema(description = "리뷰 목록", nullable = false)
        private List<ReviewResponse.reviewInfo> content;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class getTop3ReviewsDTO {
        @Schema(description = "리뷰 top3 목록", nullable = false)
        private List<ReviewResponse.topReviewInfo> content;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class reviewInfo {

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
        private List<reviewImageDTO> images;

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private List<reviewKeywordDTO> keywords;

        @Schema(description = "좋아요 여부", nullable = false, example = "false")
        private boolean isLiked;

        @Schema(description = "좋아요 수", nullable = false, example = "12")
        private int likes;

        @Schema(description = "작성 여부", nullable = false, example = "false")
        private boolean isMyReview;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class topReviewInfo {

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

        private reviewImageDTO images;

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private List<reviewKeywordDTO> keywords;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class reviewImageDTO {

        @Schema(description = "이미지 id", nullable = false, example = "1")
        private Long imageId;

        @Schema(description = "이미지 URL", nullable = false, example = "...")
        private String imageUrl;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class reviewKeywordDTO {

        @Schema(description = "키워드 id", nullable = false, example = "1")
        private Long keywordId;

        @Schema(description = "키워드 URL", nullable = false, example = "✨ 쾌적해요")
        private String keyword;
    }
}
