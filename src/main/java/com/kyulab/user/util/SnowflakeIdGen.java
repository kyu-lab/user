package com.kyulab.user.util;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

import java.util.EnumSet;

public class SnowflakeIdGen implements BeforeExecutionGenerator {

	// SNOWFLAKE 알고리즘 예제
	private static synchronized long nextId() {
		long timestamp = System.currentTimeMillis();
		long nodeId = 1; // 서버 ID (단일 서버 환경에서 고정)
		long sequence = (timestamp & 4095); // 간단한 시퀀스

		return ((timestamp - 1622476800000L) << 22) | (nodeId << 12) | sequence;
	}

	@Override
	public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
		return nextId();
	}

	@Override
	public EnumSet<EventType> getEventTypes() {
		return EnumSet.of(EventType.INSERT); // insert시에만 ID 생성함
	}

}
