package com.odiga.fiesta.festival.domain;

import static jakarta.persistence.GenerationType.*;
import static java.util.stream.Collectors.*;
import static lombok.AccessLevel.*;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import com.odiga.fiesta.common.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@AllArgsConstructor
@Getter
@SuperBuilder
@NoArgsConstructor(access = PROTECTED)
@ToString
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
	private LocalDate startDate;

	@Column(name = "end_date", nullable = false)
	private LocalDate endDate;

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
				festival.getStartDate().datesUntil(festival.getEndDate().plusDays(1))
					.map(date -> new AbstractMap.SimpleEntry<>(date, festival)))
			.collect(
				groupingBy(AbstractMap.SimpleEntry::getKey,
					mapping(AbstractMap.SimpleEntry::getValue, toList())));
	}
}
