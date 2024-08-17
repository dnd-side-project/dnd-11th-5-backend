package com.odiga.fiesta.festival.service;

import static com.odiga.fiesta.common.error.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.odiga.fiesta.IntegrationTestSupport;
import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.domain.FestivalBookmark;
import com.odiga.fiesta.festival.dto.response.FestivalBookmarkResponse;
import com.odiga.fiesta.festival.repository.FestivalBookmarkRepository;
import com.odiga.fiesta.festival.repository.FestivalRepository;
import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.repository.UserRepository;

class FestivalBookmarkServiceTest extends IntegrationTestSupport {

	@Autowired
	private FestivalBookmarkService festivalBookmarkService;

	@Autowired
	private FestivalBookmarkRepository festivalBookmarkRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FestivalRepository festivalRepository;

	@DisplayName("페스티벌 북마크 등록/해제 - 북마크 등록")
	@Test
	void updateFestivalBookmark_AddBookmark() {
		// given
		Festival festival = festivalRepository.save(createFestival());
		User user = userRepository.save(createUser());

		// when
		FestivalBookmarkResponse festivalBookmarkResponse = festivalBookmarkService.updateFestivalBookmark(user,
			festival.getId());

		// then
		assertThat(festivalBookmarkResponse.getIsBookmarked()).isTrue();
		assertEquals(1, festivalBookmarkResponse.getBookmarkCount());
	}

	@DisplayName("페스티벌 북마크 등록/해제 - 북마크 해제")
	@Test
	void updateFestivalBookmark_RemoveBookmark() {
		// given
		Festival festival = festivalRepository.save(createFestival());
		User user = userRepository.save(createUser());
		FestivalBookmark festivalBookmark = FestivalBookmark.of(user.getId(), festival.getId());
		festivalBookmarkRepository.save(festivalBookmark);

		// when
		FestivalBookmarkResponse festivalBookmarkResponse = festivalBookmarkService.updateFestivalBookmark(user,
			festival.getId());

		// then
		assertThat(festivalBookmarkResponse.getIsBookmarked()).isFalse();
		assertEquals(0, festivalBookmarkResponse.getBookmarkCount());
	}

	@DisplayName("페스티벌 북마크 등록/해제 - 존재하지 않는 유저의 경우 에러 발생 ")
	@Test
	void updateFestivalBookmark_UserNotFound() {
		// given
		Festival festival = festivalRepository.save(createFestival());

		User deletedUser = userRepository.save(createUser());
		userRepository.delete(deletedUser);

		// when // then
		assertThatThrownBy(() -> festivalBookmarkService.updateFestivalBookmark(deletedUser, festival.getId()))
			.hasMessage(USER_NOT_FOUND.getMessage());
	}

	@DisplayName("페스티벌 북마크 등록/해제 - 존재하지 않는 페스티벌에 좋아요 등록 시 에러 발생")
	@Test
	void updateFestivalBookmark_FestivalNotFound() {
		// given
		User user = userRepository.save(createUser());

		// when // then
		assertThatThrownBy(() -> festivalBookmarkService.updateFestivalBookmark(user, -1L))
			.hasMessage(FESTIVAL_NOT_FOUND.getMessage());
	}

	private Festival createFestival() {
		return Festival.builder()
			.userId(1L)
			.name("테스트 페스티벌")
			.startDate(LocalDate.of(2024, 10, 4))
			.endDate(LocalDate.of(2024, 10, 4))
			.address("부산 금정구 장전동")
			.sidoId(1L)
			.sigungu("금정구")
			.latitude(35.1719)
			.longitude(129.1741)
			.tip("페스티벌 팁")
			.homepageUrl("test.com")
			.instagramUrl("testInsta.com")
			.description("페스티벌 상세 설명")
			.ticketLink("ticket.com")
			.playtime("11:00~12:00")
			.isPending(false)
			.build();
	}

	private User createUser() {
		return User.builder()
			.userTypeId(1L)
			.roleId(1L)
			.nickname("테스트 유저")
			.statusMessage("상태 메시지")
			.profileImage("프로필 이미지 링크")
			.build();
	}
}
