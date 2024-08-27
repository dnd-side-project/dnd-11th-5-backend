package com.odiga.fiesta.review.controller;

import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.odiga.fiesta.ControllerTestSupport;
import com.odiga.fiesta.review.dto.request.ReviewCreateRequest;
import com.odiga.fiesta.review.dto.response.ReviewIdResponse;

class ReviewControllerTest extends ControllerTestSupport {

	@DisplayName("리뷰 생성 - 성공")
	@Test
	void createReview_success() throws Exception {
		ReviewCreateRequest request = ReviewCreateRequest.builder()
			.festivalId(1L)
			.rating(0.0)
			.keywordIds(List.of(1L, 2L))
			.content("Great festival!")
			.build();

		MockMultipartFile data = new MockMultipartFile(
			"data",
			"",
			"application/json",
			objectMapper.writeValueAsString(request).getBytes()
		);

		MockMultipartFile image1 = new MockMultipartFile(
			"images",
			"image1.jpg",
			"image/jpeg",
			"image1".getBytes()
		);

		MockMultipartFile image2 = new MockMultipartFile(
			"images",
			"image2.jpg",
			"image/jpeg",
			"image2".getBytes()
		);

		ReviewIdResponse response = ReviewIdResponse.builder().reviewId(1L).build();

		given(reviewService.createReview(any(Long.class), any(ReviewCreateRequest.class), any(List.class)))
			.willReturn(response);

		mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/reviews")
				.file(data)
				.file(image1)
				.file(image2)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.param("Authorization", "Bearer token"))  // assuming JWT authentication
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("리뷰 생성 성공"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.reviewId").value(1L));
	}

	@DisplayName("리뷰 생성 - 별점이 .5 단위가 아니면 에러가 발생한다.")
	@Test
	void createReview_invalidRating() throws Exception {
		ReviewCreateRequest request = ReviewCreateRequest.builder()
			.festivalId(1L)
			.rating(4.3)
			.keywordIds(List.of(1L, 2L))
			.content("Great festival!")
			.build();

		MockMultipartFile data = new MockMultipartFile(
			"data",
			"",
			"application/json",
			objectMapper.writeValueAsString(request).getBytes()
		);

		mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/reviews")
				.file(data)
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
}
