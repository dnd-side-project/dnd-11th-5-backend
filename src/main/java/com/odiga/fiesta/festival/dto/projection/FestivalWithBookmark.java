package com.odiga.fiesta.festival.dto.projection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class FestivalWithBookmark extends FestivalData {

	private Boolean isBookMarked;

}
