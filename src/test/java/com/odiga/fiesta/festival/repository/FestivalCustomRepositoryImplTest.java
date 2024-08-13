package com.odiga.fiesta.festival.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.odiga.fiesta.RepositoryTestSupport;
import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.domain.FestivalBookmark;
import com.odiga.fiesta.festival.dto.projection.FestivalWithBookmarkAndSido;
import com.odiga.fiesta.sido.domain.Sido;
import com.odiga.fiesta.user.domain.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

class FestivalCustomRepositoryImplTest extends RepositoryTestSupport {

	@Autowired
	private FestivalRepository festivalRepository;

	@PersistenceContext
	private EntityManager em;

	@DisplayName("현재 날짜에 진행 중인 페스티벌을 조회할 수 있다.")
	@Test
	void findFestivalsInDate() {
		// given
		User user = createUser();
		em.persist(user);

		Sido sido = createSido();
		em.persist(sido);

		Festival festival1 = createFestival(LocalDate.of(2024, 10, 1), LocalDate.of(2024, 10, 10), sido.getId());
		Festival festival2 = createFestival(LocalDate.of(2024, 8, 15), LocalDate.of(2024, 10, 10), sido.getId());
		Festival festival3 = createFestival(LocalDate.of(2024, 12, 25), LocalDate.of(2024, 12, 25), sido.getId());
		Festival festival4 = createFestival(LocalDate.of(2024, 10, 5), LocalDate.of(2024, 10, 10), sido.getId());

		em.persist(festival1);
		em.persist(festival2);
		em.persist(festival3);
		em.persist(festival4);

		FestivalBookmark bookmark = FestivalBookmark.builder()
			.festivalId(festival2.getId())
			.userId(user.getId())
			.build();
		em.persist(bookmark);

		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<FestivalWithBookmarkAndSido> result = festivalRepository.findFestivalsInDate(LocalDate.of(2024, 10, 4),
			pageable, user.getId());

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(2);

		List<FestivalWithBookmarkAndSido> festivals = result.getContent();

		assertEquals(2, festivals.size());

		FestivalWithBookmarkAndSido festivalWithBookmark1 = festivals.get(0);
		assertEquals(festival1.getId(), festivalWithBookmark1.getFestivalId());
		assertEquals(false, festivalWithBookmark1.getIsBookMarked());
		assertEquals("부산", festivalWithBookmark1.getSido());

		FestivalWithBookmarkAndSido festivalWithBookmark2 = festivals.get(1);
		assertEquals(festival2.getId(), festivalWithBookmark2.getFestivalId());
		assertEquals(true, festivalWithBookmark2.getIsBookMarked());
		assertEquals("부산", festivalWithBookmark2.getSido());
	}

	@DisplayName("페스티벌 페이징 조회 테스트")
	@Test
	void findFestivalsInDate_paginationn() {
		// given
		User user = createUser();
		em.persist(user);

		Sido sido = createSido();
		em.persist(sido);

		for (int i = 0; i < 30; i++) {
			Festival festival = createFestival(LocalDate.of(2024, 10, 1), LocalDate.of(2024, 10, 10), sido.getId());
			em.persist(festival);
		}

		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<FestivalWithBookmarkAndSido> result = festivalRepository.findFestivalsInDate(LocalDate.of(2024, 10, 4), pageable, user.getId());

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(30);
		assertThat(result.getTotalPages()).isEqualTo(3);
		assertThat(result.getSize()).isEqualTo(10);

		List<FestivalWithBookmarkAndSido> festivals = result.getContent();
		assertEquals(10, festivals.size());
	}

	private static Festival createFestival(LocalDate startDate, LocalDate endDate, Long sidoId) {
		return Festival.builder()
			.userId(1L)
			.name("페스티벌 이름")
			.startDate(startDate)
			.endDate(endDate)
			.address("페스티벌 주소")
			.sidoId(sidoId)
			.sigungu("시군구")
			.latitude(10.1)
			.longitude(10.1)
			.tip("페스티벌 팁")
			.homepageUrl("홈페이지 url")
			.instagramUrl("인스타그램 url")
			.fee("비용")
			.description("페스티벌 상세 설명")
			.ticketLink("티켓 링크")
			.playtime("페스티벌 진행 시간")
			.isPending(false)
			.build();
	}

	private static User createUser() {
		return User.builder()
			.userTypeId(1L)
			.nickname("유저 닉네임")
			.roleId(1L)
			.profileImage("프로필 이미지")
			.statusMessage("상태 메시지")
			.build();
	}

	private static Sido createSido() {
		return Sido.builder()
			.name("부산")
			.code(42)
			.build();
	}
}
