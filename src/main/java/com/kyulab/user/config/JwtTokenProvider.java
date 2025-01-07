package com.kyulab.user.config;

import com.kyulab.user.domain.Users;
import com.kyulab.user.service.UserCommonService;
import com.kyulab.user.service.UserRedisService;
import com.kyulab.user.util.JwtKeyUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Component
public class JwtTokenProvider {

	private final UserRedisService redisService;
	private final UserCommonService commonService;
	private final SecretKey refreshKey;
	private final SecretKey accessKey;

	@Value("${jwt.refresh-expiredTime}")
	private long rExpiredTime;

	@Value("${jwt.acess-expiredTime}")
	private long aExpiredTime;

	public JwtTokenProvider(JwtKeyUtils keyUtils, UserRedisService redisService, UserCommonService commonService) {
		this.refreshKey = keyUtils.getSecretKey();
		this.accessKey = keyUtils.getAccessKey();
		this.redisService = redisService;
		this.commonService = commonService;
	}

	// 토큰 재발급용
	public String createAccessTokenByUserId(long userId) throws IOException {
		Map<String, Object> jwtInfo = new HashMap<>();
		jwtInfo.put("alg", "HS256");
		jwtInfo.put("typ", "JWT");
		Optional<Users> users =  commonService.findByUserId(userId);
		if (users.isEmpty()) {
			throw new IOException("Invalid User");
		}
		return createJwt(jwtInfo, users.get(), aExpiredTime, accessKey);
	}

	public String createAccessToken(Users users) {
		Map<String, Object> jwtInfo = new HashMap<>();
		jwtInfo.put("alg", "HS256");
		jwtInfo.put("typ", "JWT");
		return createJwt(jwtInfo, users, aExpiredTime, accessKey);
	}

	public String createRefreshToken(Users users) {
		Map<String, Object> jwtInfo = new HashMap<>();
		jwtInfo.put("alg", "HS512");
		jwtInfo.put("typ", "JWT");
		String refreshToken = createJwt(jwtInfo, users, rExpiredTime, refreshKey);
		redisService.saveToRedis(users.getUserId(), refreshToken, rExpiredTime);
		return refreshToken;
	}

	private String createJwt(Map<String, Object> jwtInfo, Users users, long expiredTime, SecretKey key) {
		Claims claims = Jwts.claims().setSubject(String.valueOf(users.getUserId()));
		claims.put("roles", users.getUserRole());
		claims.put("username", users.getUserName());

		LocalDateTime localDateTime = LocalDateTime.now();
		Date issuedAt = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		Date expiration = Date.from(localDateTime.plusSeconds(expiredTime).atZone(ZoneId.systemDefault()).toInstant());

		return Jwts.builder()
				.setHeader(jwtInfo)
				.setClaims(claims)
				.setIssuedAt(issuedAt)
				.setExpiration(expiration)
				.signWith(key)
				.compact();
	}

	public boolean validateAccessToken(String token) {
		Jws<Claims> claims = parseAccessClaims(token);
		Date tokenExpiered = claims.getBody().getExpiration();
		if (!tokenExpiered.before(new Date())) {
			return false;
		}
		return claims.getSignature().equals(getAccessKey());
	}

	public Jws<Claims> parseAccessClaims(String token) {
		Jws<Claims> claims = null;
		try {
			claims = Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(token);
		} catch (Exception e) {
			System.out.println("Invalid token");
		}
		return claims;
	}

	private String getAccessKey() {
		return Base64.getEncoder().encodeToString(accessKey.getEncoded());
	}

	public boolean validateRefreshToken(String token) {
		Jws<Claims> claims = parseRefreshClaims(token);
		Date tokenExpiered = claims.getBody().getExpiration();
		if (!tokenExpiered.before(new Date())) {
			return false;
		}
		return claims.getSignature().equals(getRefreshKey());
	}

	public Jws<Claims> parseRefreshClaims(String token) {
		Jws<Claims> claims = null;
		try {
			claims = Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(token);
		} catch (Exception e) {
			System.out.println("Invalid token");
		}
		return claims;
	}

	public String getUserIdFromToken(String token) {
		Claims claims = Jwts.parserBuilder()
							.setSigningKey(refreshKey)
							.build()
							.parseClaimsJws(token)
							.getBody();
		return claims.getSubject();
	}

	private String getRefreshKey() {
		return Base64.getEncoder().encodeToString(accessKey.getEncoded());
	}

}
