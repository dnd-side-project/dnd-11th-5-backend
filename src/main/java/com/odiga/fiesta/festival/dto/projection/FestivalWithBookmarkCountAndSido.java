package com.odiga.fiesta.festival.dto.projection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@NoArgsConstructor
@ToString
public class FestivalWithBookmarkCountAndSido extends FestivalData {
	private String sido;
	private Long bookmarkCount;
}
