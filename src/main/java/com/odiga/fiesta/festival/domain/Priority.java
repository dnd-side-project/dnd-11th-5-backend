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
public class Priority {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "priority_id")
	private Long id;

	@Column(nullable = false)
	private String priority;

	@Builder
	public Priority(Long id, String priority) {
		this.id = id;
		this.priority = priority;
	}
}
