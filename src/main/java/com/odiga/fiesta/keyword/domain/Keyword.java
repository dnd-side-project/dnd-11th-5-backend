package com.odiga.fiesta.keyword.domain;

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
public class Keyword {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "keyword_id")
	private Long id;

	@Column(name = "keyword", nullable = false)
	private String content;

	@Builder
	private Keyword(Long id, String content) {
		this.id = id;
		this.content = content;
	}

	public static Keyword of(final Long id, final String content) {
		return new Keyword(id, content);
	}

}
