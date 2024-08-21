package com.odiga.fiesta.festival.dto.request;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.*;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FestivalCreateRequest {

	@NotBlank
	private String name;

	@NotBlank
	private String description;

	@NotNull
	@JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
	private LocalDate startDate;

	@NotNull
	@JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
	private LocalDate endDate;

	@NotBlank
	private String address;

	@NotNull
	private Double latitude;

	@NotNull
	private Double longitude;

	@NotBlank
	private String sido;

	@NotBlank
	private String sigungu;

	@NotBlank
	private String playtime;

	@NotBlank
	private String homepageUrl;

	private String instagramUrl;

	private String ticketLink;

	@NotBlank
	private String fee;

	private List<Long> categoryIds;

	private List<Long> moodIds;

	private String tip;

	@Builder

	public FestivalCreateRequest(String name, String description, LocalDate startDate, LocalDate endDate,
		String address,
		Double latitude, Double longitude, String sido, String sigungu, String playtime, String homepageUrl,
		String instagramUrl, String ticketLink, String fee, List<Long> categoryIds, List<Long> moodIds, String tip) {
		this.name = name;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.sido = sido;
		this.sigungu = sigungu;
		this.playtime = playtime;
		this.homepageUrl = homepageUrl;
		this.instagramUrl = instagramUrl;
		this.ticketLink = ticketLink;
		this.fee = fee;
		this.categoryIds = categoryIds;
		this.moodIds = moodIds;
		this.tip = tip;
	}
}
