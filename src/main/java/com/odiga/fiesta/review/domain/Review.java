package com.odiga.fiesta.review.domain;

import com.odiga.fiesta.common.domain.BaseEntity;
import com.odiga.fiesta.review.dto.request.ReviewCreateRequest;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@AllArgsConstructor
@Getter
@SuperBuilder
@NoArgsConstructor(access = PROTECTED)
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "review_id")
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "festival_id", nullable = false)
	private Long festivalId;

	@Column(name = "rating", nullable = false)
	private Integer rating;

	@Column(name = "content", nullable = false)
	private String content;

	public static Review createReview(final Long userId, final ReviewCreateRequest request) {
		return Review.builder()
			.userId(userId)
			.festivalId(request.getFestivalId())
			.rating((int)(request.getRating() * 10))
			.content(request.getContent())
			.build();
	}

	public void updateRating(final Integer rating) {
		this.rating = rating;
	}

	public void updateContent(final String content) {
		this.content = content;
	}
}
