package com.odiga.fiesta.festival.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FestivalCreateResponse {

	private Long festivalId;
	private Boolean isPending;

	@Builder
	public FestivalCreateResponse(Long festivalId, Boolean isPending) {
		this.festivalId = festivalId;
		this.isPending = isPending;
	}

	public static FestivalCreateResponse of(FestivalBasic festival) {
		return FestivalCreateResponse.builder()
			.festivalId(festival.getFestivalId())
			.isPending(false) // TODO 관리자 구현 전까진 무조건 true
			.build();
	}
}
