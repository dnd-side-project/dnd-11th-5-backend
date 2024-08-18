package com.odiga.fiesta.festival.dto.projection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@NoArgsConstructor
public class FestivalWithSido extends FestivalData {

	private String sido;

	public static FestivalWithSido of(FestivalWithBookmarkCountAndSido festival) {
		return FestivalWithSido.builder()
			.festivalId(festival.getFestivalId())
			.name(festival.getName())
			.sigungu(festival.getSigungu())
			.startDate(festival.getStartDate())
			.endDate(festival.getEndDate())
			.sido(festival.getSido())
			.build();
	}

}
