package com.odiga.fiesta.festival.domain;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.odiga.fiesta.common.domain.BaseEntity;
import com.odiga.fiesta.festival.dto.response.FestivalSimpleResponse;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
public class Festival extends BaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "festival_id")
	private Long id;

	@Column(name = "user_id")
	private Long userId;

	@Column(nullable = false)
	private String name;

	@Column(name = "start_date", nullable = false)
	private LocalDateTime startDate;

	@Column(name = "end_date", nullable = false)
	private LocalDateTime endDate;

	@Column(nullable = false)
	private String address;

	@Column(name = "sido_id", nullable = false)
	private Long sidoId;

	@Column(nullable = false)
	private String sigungu;

	@Column(nullable = false)
	private double latitude;

	@Column(nullable = false)
	private double longitude;

	@Column(nullable = false)
	private String tip;

	@Column(name = "homepage_url", length = 1024)
	private String homepageUrl;

	@Column(name = "instagram_url", length = 1024)
	private String instagramUrl;

	private String fee;

	@Column(nullable = false)
	private String description;

	@Column(name = "ticket_link", length = 1024)
	private String ticketLink;

	private String playtime;

	@Column(name = "is_pending", nullable = false)
	private boolean isPending;

	public static Map<LocalDate, List<Festival>> getGroupedByDate(List<Festival> festivals) {
		return festivals.stream()
			.flatMap(festival ->
				festival.getStartDate().toLocalDate().datesUntil(festival.getEndDate().toLocalDate().plusDays(1))
					.map(date -> new AbstractMap.SimpleEntry<>(date, festival)))
			.collect(
				Collectors.groupingBy(entry -> entry.getKey(),
					Collectors.mapping(entry -> entry.getValue(), Collectors.toList())));
	}
}
