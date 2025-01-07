package com.kyulab.user.util;

import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class JwtKeyUtils {

	@Value("${jwt.secret}")
	private String refresKeyOrigin;
	private SecretKey secretKey;

	@Value("${jwt.access}")
	private String accessKeyOrigin;
	private SecretKey accessKey;

	@PostConstruct
	public void createKey() {
		byte[] decodedKey = Base64.getDecoder().decode(refresKeyOrigin);
		this.secretKey = new SecretKeySpec(decodedKey, SignatureAlgorithm.HS512.getJcaName());

		decodedKey = Base64.getDecoder().decode(accessKeyOrigin);
		this.accessKey = new SecretKeySpec(decodedKey, SignatureAlgorithm.HS256.getJcaName());
	}

	@Bean
	public SecretKey getSecretKey() {
		return this.secretKey;
	}

	@Bean
	public SecretKey getAccessKey() {
		return this.accessKey;
	}

}
