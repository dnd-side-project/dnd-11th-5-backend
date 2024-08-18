package com.odiga.fiesta.log.domain;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import com.odiga.fiesta.common.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "log_image")
@Getter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
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
