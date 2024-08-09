package com.odiga.fiesta.global.domain;

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

	@Column(nullable = false)
	private String category;

	@Builder
	public Category(Long id, String category) {
		this.id = id;
		this.category = category;
	}
}
