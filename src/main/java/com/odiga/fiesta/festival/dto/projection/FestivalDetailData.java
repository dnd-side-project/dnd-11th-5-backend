package com.odiga.fiesta.festival.dto.projection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@ToString
public class FestivalDetailData extends FestivalData {

	private String description;
	private String address;
	private String sido;
	private String tip;
	private String homepageUrl;
	private String instagramUrl;
	private String fee;
	private String ticketLink;
	private Long bookmarkCount;
	private Boolean isBookmarked;
}

