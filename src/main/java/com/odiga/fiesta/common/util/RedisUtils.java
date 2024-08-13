package com.odiga.fiesta.common.util;

import static java.util.concurrent.TimeUnit.*;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

// https://developer-nyong.tistory.com/21

@Component
@RequiredArgsConstructor
public class RedisUtils {

	private final RedisTemplate<String, Object> redisTemplate;

	public void setData(String key, String value, Long expiredTime) {
		redisTemplate.opsForValue().set(key, value, expiredTime, MILLISECONDS);
	}

	public String getData(String key) {
		return (String)redisTemplate.opsForValue().get(key);
	}

	public void deleteData(String key) {
		redisTemplate.delete(key);
	}
}
