package com.kyulab.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRedisService {

	private final StringRedisTemplate redisTemplate;

	public void saveToRedis(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
	}

	public String getFromRedis(String key) {
		return redisTemplate.opsForValue().get(key);
	}

}
