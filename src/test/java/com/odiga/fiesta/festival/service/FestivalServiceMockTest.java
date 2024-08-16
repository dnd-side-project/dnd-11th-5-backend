package com.odiga.fiesta.festival.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.transaction.annotation.Transactional;

import com.odiga.fiesta.MockTestSupport;
import com.odiga.fiesta.common.PageResponse;
import com.odiga.fiesta.common.util.RedisUtils;
import com.odiga.fiesta.festival.dto.response.FestivalBasic;

@Transactional
public class FestivalServiceMockTest extends MockTestSupport {

	@Mock
	private RedisUtils redisUtils;

	@InjectMocks
	private FestivalService festivalService;

	private static final String RANKING_KEY = "testKey";
	private FestivalBasic searchItem;

	@BeforeEach
	public void setUp() {
		searchItem = new FestivalBasic(1L, "락페");
	}

	@Test
	public void testUpdateSearchRanking_NewScore() {
		String rankingKey = "testKey";
		FestivalBasic searchItem = new FestivalBasic(1L, "락페");
		final Double SCORE_INCREMENT_AMOUNT = 1.0;

		when(redisUtils.zScore(anyString(), any(FestivalBasic.class))).thenReturn(null);

		festivalService.updateSearchRanking(rankingKey, searchItem);

		verify(redisUtils, times(1)).zAdd(eq(rankingKey), eq(searchItem), eq(SCORE_INCREMENT_AMOUNT));
	}

	@Test
	public void testUpdateSearchRanking_IncrementScore() {
		String rankingKey = "testKey";
		FestivalBasic searchItem = new FestivalBasic(1L, "searchItem");
		final Double SCORE_INCREMENT_AMOUNT = 1.0;
		final Double initialScore = 2.0;

		when(redisUtils.zScore(anyString(), any(FestivalBasic.class))).thenReturn(initialScore);

		festivalService.updateSearchRanking(rankingKey, searchItem);

		verify(redisUtils, times(1)).zAdd(eq(rankingKey), eq(searchItem), eq(initialScore + SCORE_INCREMENT_AMOUNT));
	}

	@Test
	public void testGetTrendingFestival_WithResults() {
		Long page = 0L;
		Integer size = 10;

		ZSetOperations.TypedTuple<FestivalBasic> tuple = mock(ZSetOperations.TypedTuple.class);
		when(tuple.getValue()).thenReturn(searchItem);

		Set<ZSetOperations.TypedTuple<FestivalBasic>> set = new HashSet<>();
		set.add(tuple);

		when(redisUtils.zRevrange(anyString(), anyLong(), anyLong())).thenReturn(set);
		when(redisUtils.zSize(anyString())).thenReturn(1L);

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
