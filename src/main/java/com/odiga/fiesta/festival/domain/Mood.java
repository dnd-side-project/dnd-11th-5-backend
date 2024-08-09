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
public class Mood {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "mood_id")
	private Long id;

	@Column(nullable = false)
	private String mood;

	@Builder
	public Mood(Long id, String mood) {
		this.id = id;
		this.mood = mood;
	}
}
