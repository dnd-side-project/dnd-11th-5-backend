package com.odiga.fiesta.badge.domain;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor(access = PROTECTED)
@Table(name = "user_badge", indexes = {
	@Index(name = "idx_user_badge_user_id", columnList = "user_id"),
	@Index(name = "idx_user_badge_badge_id", columnList = "badge_id")
})
public class UserBadge extends BaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "user_badge_id")
	private Long id;

	@Column(name = "badge_id")
	private Long badgeId;

	@Column(name = "user_id")
	private Long userId;
}
