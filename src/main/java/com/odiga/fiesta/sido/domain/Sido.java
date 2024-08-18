package com.odiga.fiesta.sido.domain;

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
@Builder
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
@Table(name = "sido")
public class Sido {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "sido_id")
	private Long id;

	@Column(name = "sido")
	private String name;

	private Integer code;
}
