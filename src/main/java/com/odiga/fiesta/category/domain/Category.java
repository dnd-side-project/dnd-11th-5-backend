package com.odiga.fiesta.category.domain;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Category {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "category_id")
	private Long id;

	@Column(name = "category", nullable = false)
	private String name;

	@Column(name = "emoji", nullable = false)
	private String emoji;

	@Builder
	public Category(Long id, String name, String emoji) {
		this.id = id;
		this.name = name;
		this.emoji = emoji;
	}
}
