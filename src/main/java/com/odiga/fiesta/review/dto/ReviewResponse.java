package com.odiga.fiesta.review.dto;

import com.odiga.fiesta.keyword.domain.Keyword;
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

        @Schema(description = "현재 페이지에서 시작하는 요소의 index 번호", nullable = false, example = "0")
        private int offset;

        @Schema(description = "현재 페이지 넘버", nullable = false, example = "0")
        private int pageNumber;

        @Schema(description = "페이지 사이즈", nullable = false, example = "6")
        private int pageSize;

        @Schema(description = "전체 요소 수", nullable = false, example = "3")
        private long totalElements;

        @Schema(description = "전체 페이지 수", nullable = false, example = "1")
        private int totalPages;
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
        private int rating;

        @Schema(description = "이미지 URL", nullable = false, example = "...")
        private List<String> images;

        @Schema(description = "키워드", nullable = false, example = "✨ 쾌적해요")
        private List<String> keywords;

        @Schema(description = "좋아요 여부", nullable = false, example = "false")
        private boolean liked;

        @Schema(description = "좋아요 수", nullable = false, example = "12")
        private int likes;

        @Schema(description = "작성 여부", nullable = false, example = "false")
        private boolean writtenByMe;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
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
        private int rating;

        @Schema(description = "이미지 URL", nullable = false, example = "...")
        private String images;

        @Schema(description = "키워드", nullable = false, example = "✨ 쾌적해요")
        private List<String> keywords;
    }
}
