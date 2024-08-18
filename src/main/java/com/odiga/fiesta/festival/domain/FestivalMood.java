package com.odiga.fiesta.festival.domain;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "festival_mood")
@Builder
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class FestivalMood {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "festival_mood_id", nullable = false)
	private Long id;

	@Column(name = "festival_id", nullable = false)
	private Long festivalId;

	@Column(name = "mood_id", nullable = false)
	private Long moodId;
}
