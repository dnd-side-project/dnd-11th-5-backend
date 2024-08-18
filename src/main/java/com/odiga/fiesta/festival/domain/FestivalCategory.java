package com.odiga.fiesta.festival.domain;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import com.odiga.fiesta.common.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Table(name = "festival_category")
@SuperBuilder
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class FestivalCategory extends BaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "festival_category_id", nullable = false)
	private Long id;

	@Column(name = "festival_id", nullable = false)
	private Long festivalId;

	@Column(name = "category_id", nullable = false)
	private Long categoryId;
}
