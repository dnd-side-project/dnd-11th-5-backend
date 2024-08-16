package com.odiga.fiesta.common.util;

import static java.util.concurrent.TimeUnit.*;

import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

// https://developer-nyong.tistory.com/21

@Component
@RequiredArgsConstructor
public class RedisUtils<T> {

	private final RedisTemplate<String, T> redisTemplate;

	public void setData(String key, T value, Long expiredTime) {
		redisTemplate.opsForValue().set(key, value, expiredTime, MILLISECONDS);
	}

	public T getData(String key, Class<T> clazz) {
		Object value = redisTemplate.opsForValue().get(key);
		return clazz.cast(value);
	}

	public void deleteData(String key) {
		redisTemplate.delete(key);
	}

	// 실시간 검색어 관련
	public Double zScore(String key, T member) {
		return redisTemplate.opsForZSet().score(key, member);
	}

	public void zAdd(String key, T target, Double score) {
		redisTemplate.opsForZSet().add(key, target, score);
	}

	public Set<ZSetOperations.TypedTuple<T>> zRevrange(String key, long start, long end) {
		return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
	}

	public Long zSize(String key){
		return redisTemplate.opsForZSet().size(key);
	}

}
