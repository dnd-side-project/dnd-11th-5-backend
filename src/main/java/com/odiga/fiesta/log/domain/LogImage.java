package com.odiga.fiesta.log.domain;

import static jakarta.persistence.GenerationType.*;

import com.odiga.fiesta.common.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Entity
@Table(name = "log_image")
@Getter
public class LogImage extends BaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "log_image_id")
	private Long id;

	@Column(name = "log_id")
	@NotNull
	private Long logId;

	@Column(name = "image_url", length = 2083)
	@NotNull
	private String imageUrl;
}
