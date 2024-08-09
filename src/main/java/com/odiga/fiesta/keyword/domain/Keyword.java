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

	@Column(nullable = false)
	private String keyword;

	@Builder
	private Keyword(Long id, String keyword) {
		this.id = id;
		this.keyword = keyword;
	}

	public static Keyword of(final Long id, final String keyword) {
		return new Keyword(id, keyword);
	}

}
