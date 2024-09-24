package com.odiga.fiesta.badge.domain;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@AllArgsConstructor
@Getter
@SuperBuilder
@NoArgsConstructor(access = PROTECTED)
@ToString
@Table(name = "badge", indexes = {
	@Index(name = "idx_badge_type", columnList = "type")
})
public class Badge {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "badge_id")
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String description;

	@Column(name = "image_url", nullable = false)
	private String imageUrl;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private BadgeType type;
}
