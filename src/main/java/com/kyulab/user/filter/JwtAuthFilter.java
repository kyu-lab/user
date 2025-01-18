package com.kyulab.user.filter;

import com.kyulab.user.domain.role.TokenType;
import com.kyulab.user.dto.TokenDTO;
import com.kyulab.user.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

	private final TokenService tokenService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
					FilterChain filterChain) throws ServletException, IOException {
		final String token = tokenService.getTokenFromHeader(request);
		if (!token.isEmpty()) {
			if (tokenService.validToken(token)) {
				authenticationUser(request, tokenService.parseDataByToken(token), token);
			} else {
				String userId = tokenService.getUserIdFromToken(token);
				String refreshToken = tokenService.getRefreshToken(userId);
				if (refreshToken != null) { // 리프레쉬 토큰이 유효하다면
					TokenDTO tokenDTO = tokenService.parseDataByToken(refreshToken);
					String accessToken = tokenService.createAccessToken(tokenDTO);
					authenticationUser(request, tokenService.parseDataByToken(accessToken), accessToken);
					String cookie = tokenService.makeCookieString(TokenType.ACCESS_TOKEN.getType(), accessToken, false);
					setToken(response, cookie);
				} else {
					String clearToken = tokenService.makeCookieString(TokenType.ACCESS_TOKEN.getType(), "", true);
					setToken(response, clearToken);
					return;
				}
			}
		}
		filterChain.doFilter(request, response);
	}

	/**
	 * 사용자 인증
	 * @param request 요청
	 * @param tokenDTO 토큰 정보
	 * @param token 토큰
	 */
	private void authenticationUser(HttpServletRequest request, TokenDTO tokenDTO, String token) {
		UsernamePasswordAuthenticationToken authToken =
			new UsernamePasswordAuthenticationToken(tokenDTO.userName(), token, new ArrayList<>()
		);
		authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authToken);
	}

	private void setToken(HttpServletResponse response, String value) {
		response.setHeader(HttpHeaders.SET_COOKIE, value);
		if (value.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

}
