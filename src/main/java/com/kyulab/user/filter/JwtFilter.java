package com.kyulab.user.filter;

import com.kyulab.user.config.JwtTokenProvider;
import com.kyulab.user.service.UserRedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtTokenProvider tokenProvider;
	private final UserRedisService redisService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String requestURI = request.getRequestURI();
		if (requestURI.startsWith("/user")) {
			String token = checkBearer(request);
			if (!requestURI.startsWith("/user/account") && token == null) {
				if (requestURI.startsWith("/user/auth")) {
					// 테스틀 위해서 잠시 열어둠
				} else if (!tokenProvider.validateAccessToken(token)) {
					String refreshToken = "";
					if (request.getCookies() != null) {
						for (Cookie c : request.getCookies()) {
							if (c.getName().equals("refreshToken")) {
								refreshToken = c.getValue();
							}
						}
					}

					if (!tokenProvider.validateRefreshToken(refreshToken)) {
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						return;
					}
					String userId = tokenProvider.getUserIdFromToken(refreshToken);
					String redisToken = redisService.getFromRedis(userId);
					if (!refreshToken.equals(redisToken)) {
						response.setStatus(HttpServletResponse.SC_FORBIDDEN);
						return;
					}

					String newAccessToken = tokenProvider.createAccessTokenByUserId(Long.parseLong(userId));
					response.setHeader("NEW_ACESS_TOKEN", newAccessToken);
					if (tokenProvider.validateRefreshToken(refreshToken)) {
						ResponseCookie responseCookie = ResponseCookie.from("refreshToken")
								.value(refreshToken)
								.httpOnly(true)
								.secure(false)
								.maxAge(1000)
								.build();
						response.setHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
					}
				}
			}
		}

		filterChain.doFilter(request, response);
	}

	private String checkBearer(HttpServletRequest request) {
		String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (bearerToken == null || bearerToken.isBlank()) {
			return null;
		}
		return bearerToken.substring(7);
	}
}
