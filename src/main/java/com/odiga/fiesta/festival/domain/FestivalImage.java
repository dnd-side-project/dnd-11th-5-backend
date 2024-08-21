package com.odiga.fiesta.festival.domain;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import com.odiga.fiesta.common.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor
public class FestivalImage extends BaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "festival_image_id")
	private Long id;

	@Column(name = "festival_id", nullable = false)
	private Long festivalId;

	@Column(name = "image_url", length = 2083, nullable = false)
	private String imageUrl;

	public static FestivalImage of(Long festivalId, String imageUrl) {
		return FestivalImage.builder()
			.festivalId(festivalId)
			.imageUrl(imageUrl)
			.build();
	}
}
