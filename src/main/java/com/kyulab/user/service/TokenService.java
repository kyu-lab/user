package com.kyulab.user.service;

import com.kyulab.user.domain.Users;
import com.kyulab.user.domain.role.TokenType;
import com.kyulab.user.dto.TokenDTO;
import com.kyulab.user.util.TokenSecretUtil;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Refresh Token과 관련
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {


	@Value("${jwt.refresh-expiredTime}")
	private long refreshExpired;

	@Value("${jwt.acess-expiredTime}")
	private long accessExpired;

	private final TokenSecretUtil tokenSecretUtil;
	private final StringRedisTemplate redisTemplate;

	public String createRefreshToken(Users users) {
		Map<String, Object> jwtInfo = new HashMap<>();
		jwtInfo.put(JwsHeader.ALGORITHM, SignatureAlgorithm.HS512.getValue());
		jwtInfo.put(JwsHeader.TYPE, JwsHeader.JWT_TYPE);

		Claims claims = Jwts.claims().setSubject(String.valueOf(users.getId()));
		claims.put("userName", users.getUsername());
		claims.put("roles", users.getAuthorities());

		LocalDateTime localDateTime = LocalDateTime.now();
		Date issuedAt = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		Date expiration = Date.from(localDateTime.plusSeconds(refreshExpired).atZone(ZoneId.systemDefault()).toInstant());

		return Jwts.builder()
				.setHeader(jwtInfo)
				.setClaims(claims)
				.setIssuedAt(issuedAt)
				.setExpiration(expiration)
				.signWith(tokenSecretUtil.getSecretKey())
				.compact();
	}

	public String createAccessToken(TokenDTO tokenDTO) {
		Map<String, Object> jwtInfo = new HashMap<>();
		jwtInfo.put(JwsHeader.ALGORITHM, SignatureAlgorithm.HS512.getValue());
		jwtInfo.put(JwsHeader.TYPE, JwsHeader.JWT_TYPE);

		Claims claims = Jwts.claims().setSubject(String.valueOf(tokenDTO.id()));
		claims.put("userName", tokenDTO.userName());
		claims.put("roles", tokenDTO.roles());

		LocalDateTime localDateTime = LocalDateTime.now();
		Date issuedAt = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		Date expiration = Date.from(localDateTime.plusSeconds(accessExpired).atZone(ZoneId.systemDefault()).toInstant());

		return Jwts.builder()
				.setHeader(jwtInfo)
				.setClaims(claims)
				.setIssuedAt(issuedAt)
				.setExpiration(expiration)
				.signWith(tokenSecretUtil.getSecretKey())
				.compact();
	}

	/**
	 * 토큰의 서명과 유효기간이 일치하는지 확인한다.
	 * @param token 토큰
	 * @return 유효 토큰 여부
	 * @throws JwtException 토큰을 못읽을 경우
	 */
	public boolean validToken(String token) throws JwtException {
		try {
			Jws<Claims> claims = Jwts.parserBuilder()
					.setSigningKey(tokenSecretUtil.getSecretKey())
					.build().parseClaimsJws(token);
			Date tokenExpiered = claims.getBody().getExpiration();
			return tokenExpiered.after(new Date());
		} catch (JwtException e) {
			log.warn("Invalid token : " + e.getMessage());
			return false;
		}
	}

	/**
	 * 토큰으로부터 userId을 가져온다.
	 * @param token 토큰
	 * @return userId
	 */
	public String getUserIdFromToken(String token) {
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(tokenSecretUtil.getSecretKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
		return claims.getSubject();
	}

	/**
	 * 리프레쉬 토큰으로 액세스 토큰 재발급을 위한 객체를 생성한다.
	 * @param token 리프레쉬 토큰
	 * @return 토큰에서 추출한 정보
	 */
	public TokenDTO parseDataByToken(String token) {
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(tokenSecretUtil.getSecretKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
				
		return new TokenDTO(
				claims.getSubject(),
				(String) claims.get("userName"),
				(Collection<? extends GrantedAuthority>) claims.get("roles")
		);
	}

	/**
	 * header에서 액세스 토큰을 가져온다.
	 */
	public String getTokenFromHeader(HttpServletRequest request) {
		String accessToken = "";
		if (request.getCookies() != null) {
			for (Cookie c : request.getCookies()) {
				if (c.getName().equals(TokenType.ACCESS_TOKEN.getType())) {
					accessToken = c.getValue();
					break;
				}
			}
		}
		return accessToken;
	}

	/**
	 * 쿠키를 만들고 문자열로 반환한다.
	 * @param cookieName 이름
	 * @param value 값
	 * @param isReset 초기화 쿠키 여부
	 * @return 문자열로 변환된 쿠키
	 */
	public String makeCookieString(String cookieName, String value, boolean isReset) {
		return ResponseCookie
				.from(cookieName)
				.value(value)
				.httpOnly(true)
				.secure(true)
				.maxAge(isReset ? 0 : accessExpired)
				.path("/")
				.build()
				.toString();
	}

	private final String REFRESH_TOKEN_PREFIX = "F";

	/**
	 * 사용자의 리프레쉬 토큰을 저장한다.
	 * F + {사용자 id}
	 * @param id 사용자 id
	 * @param refreshToken 리프레쉬 토큰
	 */
	public void storeRefreshToken(long id, String refreshToken) {
		redisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + id, refreshToken, refreshExpired, TimeUnit.SECONDS);
	}

	/**
	 * redis를 검색해 사용자의 리프레쉬 토큰이 일치하는지 확인한다.
	 * @param id 사용자 id
	 * @return 검증여부
	 */
	public String getRefreshToken(String id) {
		return redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + id);
	}

	/**
	 * 리프레쉬 토큰을 제거한다.
	 * @param id 사용자 id
	 */
	public void deleteRefreshToken(String id) {
		redisTemplate.delete(REFRESH_TOKEN_PREFIX + id);
	}

}
