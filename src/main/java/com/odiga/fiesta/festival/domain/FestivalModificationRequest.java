package com.odiga.fiesta.festival.domain;

import static jakarta.persistence.GenerationType.*;

import com.odiga.fiesta.common.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "festival_modification_request")
public class FestivalModificationRequest extends BaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "festival_modification_id")
	private Long id;

	@Column(name = "festival_id", nullable = false)
	private Long festivalId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "content", length = 500, nullable = false)
	private String content;
}
