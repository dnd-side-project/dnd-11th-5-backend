package com.odiga.fiesta.festival.dto.projection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@NoArgsConstructor
public class FestivalWithSido extends FestivalData {

	private String sido;
}
