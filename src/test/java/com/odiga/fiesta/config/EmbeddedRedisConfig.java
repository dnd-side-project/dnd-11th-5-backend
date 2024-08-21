package com.odiga.fiesta.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import redis.embedded.RedisServer;

// https://devoong2.tistory.com/entry/Springboot-Redis-%ED%85%8C%EC%8A%A4%ED%8A%B8-%ED%99%98%EA%B2%BD-%EA%B5%AC%EC%B6%95%ED%95%98%EA%B8%B0-Embedded-Redis-TestContainer

@TestConfiguration
public class EmbeddedRedisConfig {
	private RedisServer redisServer;

	public EmbeddedRedisConfig(@Value("${spring.data.redis.port}") int port) throws IOException {
		this.redisServer = new RedisServer(port);
	}

	@PostConstruct
	public void startRedis() {
		this.redisServer.start();
	}

	@PreDestroy
	public void stopRedis() {
		this.redisServer.stop();
	}
}
