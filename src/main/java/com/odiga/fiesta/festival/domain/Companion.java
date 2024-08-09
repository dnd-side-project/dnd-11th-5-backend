package com.odiga.fiesta.festival.domain;

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
public class Companion {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "companion_type_id")
	private Long id;

	@Column(name = "companion_type", nullable = false)
	private String companionType;

	@Builder
	public Companion(Long id, String companionType) {
		this.id = id;
		this.companionType = companionType;
	}
}
