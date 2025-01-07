package com.kyulab.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserRedisService {

	private final StringRedisTemplate redisTemplate;

	public void saveToRedis(long key, String value, Long expiredTime) {
		redisTemplate.opsForValue().set(String.valueOf(key), value, expiredTime, TimeUnit.MILLISECONDS);
	}

	public String getFromRedis(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public void deleteFromRedis(String key) {
		redisTemplate.delete(key);
	}

}
