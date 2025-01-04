package com.kyulab.user.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserKafkaService {

	@KafkaListener(topics = "user-group", groupId = "user-group")
	public void consume(String message) {
		System.out.println("Consumed message: " + message);
	}

}
