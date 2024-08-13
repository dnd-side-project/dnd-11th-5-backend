package com.odiga.fiesta.festival.dto.projection;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class FestivalData {

	private Long festivalId;
	private String name;
	private String sigungu;
	private LocalDate startDate;
	private LocalDate endDate;
}
