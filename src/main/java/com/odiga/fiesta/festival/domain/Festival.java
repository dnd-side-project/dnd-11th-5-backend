package com.odiga.fiesta.festival.domain;

import static jakarta.persistence.GenerationType.*;
import static java.util.stream.Collectors.*;
import static lombok.AccessLevel.*;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import com.odiga.fiesta.common.domain.BaseEntity;
import com.odiga.fiesta.festival.dto.request.FestivalCreateRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@AllArgsConstructor
@Getter
@SuperBuilder
@NoArgsConstructor(access = PROTECTED)
@Table(name = "festival", indexes = {
	@Index(name = "idx_festival_user_id", columnList = "user_id")
})
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

	@Column(name = "tip", length = 1000)
	private String tip;

	@Column(name = "homepage_url", length = 2083)
	private String homepageUrl;

	@Column(name = "instagram_url", length = 2083)
	private String instagramUrl;

	@Column(name = "fee", length = 1000)
	private String fee;

	@Column(length = 2000)
	private String description;

	@Column(name = "ticket_link", length = 2083)
	private String ticketLink;

	@Column(name = "playtime", length = 1000)
	private String playtime;

	@Column(name = "is_pending", nullable = false)
	private boolean isPending;

	@Column(name = "content_id")
	private String contentId;

	public static Map<LocalDate, List<Festival>> getGroupedByDate(List<Festival> festivals) {
		return festivals.stream()
			.flatMap(festival ->
				festival.getStartDate().datesUntil(festival.getEndDate().plusDays(1))
					.map(date -> new AbstractMap.SimpleEntry<>(date, festival)))
			.collect(
				groupingBy(AbstractMap.SimpleEntry::getKey,
					mapping(AbstractMap.SimpleEntry::getValue, toList())));
	}

	public static Festival of(FestivalCreateRequest festival, Long userId, Long sidoId) {
		return Festival.builder()
			.userId(userId)
			.name(festival.getName())
			.startDate(festival.getStartDate())
			.endDate(festival.getEndDate())
			.address(festival.getAddress())
			.sidoId(sidoId)
			.sigungu(festival.getSigungu())
			.latitude(festival.getLatitude())
			.longitude(festival.getLongitude())
			.tip(festival.getTip())
			.homepageUrl(festival.getHomepageUrl())
			.instagramUrl(festival.getInstagramUrl())
			.fee(festival.getFee())
			.description(festival.getDescription())
			.ticketLink(festival.getTicketLink())
			.playtime(festival.getPlaytime())
			.isPending(false) // TODO 관리자 api 구현 전 까진 false
			.build();
	}
}
