package com.odiga.fiesta.festival.dto.response;

import java.time.LocalDate;

import com.odiga.fiesta.festival.dto.projection.FestivalWithSido;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class FestivalInfo extends FestivalBasic {

	private String sido; // 시도 행정 구역 이름
	private String sigungu; // 시군구 행정 구역 이름
	private String thumbnailImage;
	private LocalDate startDate;
	private LocalDate endDate;

	public FestivalInfo(Long festivalId, String name, String sido, String sigungu, String thumbnailImage,
		LocalDate startDate, LocalDate endDate) {
		super(festivalId, name);
		this.sido = sido;
		this.sigungu = sigungu;
		this.thumbnailImage = thumbnailImage;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public static FestivalInfo of(FestivalWithSido festival, String thumbnailImage) {
		return FestivalInfo.builder()
			.festivalId(festival.getFestivalId())
			.name(festival.getName())
			.sido(festival.getSido())
			.sigungu(festival.getSigungu())
			.thumbnailImage(thumbnailImage)
			.startDate(festival.getStartDate())
			.endDate(festival.getEndDate())
			.build();
	}

}
