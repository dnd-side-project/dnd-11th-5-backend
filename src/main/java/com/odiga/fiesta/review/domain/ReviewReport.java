package com.odiga.fiesta.review.domain;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import com.odiga.fiesta.common.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review_report",
	indexes = {
		@Index(name = "idx_festival_modification_review_id", columnList = "review_id"),
		@Index(name = "idx_festival_modification_is_pending", columnList = "is_pending")
	})
@Getter
@NoArgsConstructor(access = PROTECTED)
@Builder
@AllArgsConstructor
public class ReviewReport extends BaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "review_report_id")
	private Long id;

	@Column(name = "review_id", nullable = false)
	private Long reviewId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "description", nullable = false, length = 500)
	private String description;

	@Column(name = "is_pending", nullable = false, columnDefinition = "BIT(1) DEFAULT 1")
	private Boolean isPending;
}
