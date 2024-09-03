package com.odiga.fiesta.festival.domain;

import static jakarta.persistence.GenerationType.*;

import com.odiga.fiesta.common.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
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

	@Builder
	public FestivalModificationRequest(Long id, Long festivalId, Long userId,
		String content) {
		this.id = id;
		this.festivalId = festivalId;
		this.userId = userId;
		this.content = content;
	}
}
