package com.kyulab.user.util;

import org.springframework.stereotype.Component;

@Component
public class UserSecurityUtil {

	private final long epoch = 1622476800000L;
	private final long nodeId = 1; // 단일 서버가 아닐 경우 꼭 수정
	private long sequence = 0L;
	private long lastTimestamp = -1L;

	// SNOWFLAKE 알고리즘 예제
	public synchronized long nextId() {
		long timestamp = System.currentTimeMillis();

		if (timestamp == lastTimestamp) {
			sequence = (sequence + 1) & 4095;
			if (sequence == 0) {
				while (timestamp <= lastTimestamp) {
					timestamp = System.currentTimeMillis();
				}
			}
		} else {
			sequence = 0;
		}

		lastTimestamp = timestamp;
		return ((timestamp - epoch) << 22) | (nodeId << 12) | sequence;
	}

}
