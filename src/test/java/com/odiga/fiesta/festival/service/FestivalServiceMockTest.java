package com.odiga.fiesta.festival.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.data.redis.core.ZSetOperations.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.transaction.annotation.Transactional;

import com.odiga.fiesta.MockTestSupport;
import com.odiga.fiesta.common.PageResponse;
import com.odiga.fiesta.common.util.RedisUtils;
import com.odiga.fiesta.festival.domain.Festival;
import com.odiga.fiesta.festival.dto.response.FestivalBasic;
import com.odiga.fiesta.festival.repository.FestivalRepository;

@Transactional
class FestivalServiceMockTest extends MockTestSupport {

	@Mock
	private RedisUtils redisUtils;

	@Mock
	private FestivalRepository festivalRepository;

	@InjectMocks
	private FestivalService festivalService;

	private static final String RANKING_KEY = "testKey";
	private static final Double SCORE_INCREMENT_AMOUNT = 1.0;
	private FestivalBasic searchItem;
	private String itemIdToString;

	@BeforeEach
	public void setUp() {
		searchItem = new FestivalBasic(1L, "락페");
		itemIdToString = searchItem.getFestivalId().toString();
	}

	@DisplayName("페스티벌 실시간 랭킹 집계 - 페스티벌이 처음 검색되는 경우")
	@Test
	public void testUpdateSearchRanking_NewScore() {
		// given // when
		when(redisUtils.zScore(RANKING_KEY, itemIdToString)).thenReturn(null);
		festivalService.updateSearchRanking(RANKING_KEY, searchItem);
		// then
		verify(redisUtils, times(1)).zAdd(eq(RANKING_KEY), eq(itemIdToString), eq(SCORE_INCREMENT_AMOUNT));
	}

	@DisplayName("페스티벌 실시간 랭킹 집계 - 페스티벌의 점수가 누적되는 경우")
	@Test
	public void testUpdateSearchRanking_IncrementScore() {
		// given
		final Double initialScore = 2.0;

		// when
		when(redisUtils.zScore(RANKING_KEY, itemIdToString)).thenReturn(initialScore);
		festivalService.updateSearchRanking(RANKING_KEY, searchItem);

		// then
		verify(redisUtils, times(1)).zAdd(eq(RANKING_KEY), eq(itemIdToString),
			eq(initialScore + SCORE_INCREMENT_AMOUNT));
	}

	@DisplayName("페스티벌 실시간 랭킹 확인")
	@Test
	void testGetTrendingFestival_WithResults() {
		Long page = 0L;
		Integer size = 10;

		TypedTuple tuple = mock(TypedTuple.class);
		when(tuple.getValue()).thenReturn(itemIdToString);

		Set<TypedTuple> set = new HashSet<>();
		set.add(tuple);

		when(redisUtils.zRevrange(anyString(), anyLong(), anyLong())).thenReturn(set);
		when(redisUtils.zSize(anyString())).thenReturn(1L);
		when(festivalRepository.findAllById(anyIterable())).thenReturn(List.of(
			Festival.builder()
				.id(searchItem.getFestivalId())
				.name(searchItem.getName())
				.build()
		));

		PageResponse<FestivalBasic> response = festivalService.getTrendingFestival(RANKING_KEY, page, size);

		assertEquals(1, response.getContent().size());
		assertEquals(1L, response.getTotalElements());
		assertEquals(1, response.getTotalElements());
		assertEquals(page, response.getPageNumber());
		assertEquals(1, response.getTotalPages());

		FestivalBasic festival = response.getContent().get(0);
		assertEquals(searchItem.getFestivalId(), festival.getFestivalId());
		assertEquals(searchItem.getName(), festival.getName());
	}
}
