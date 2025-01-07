package com.kyulab.user.config;

import com.kyulab.user.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtFilter jwtFilter;

	/**
	 * 로컬에서 사용할 개발툴 주소 모음
	 * swagger, h2
	 */
	private final String[] localDevTool = {
			"/swagger/**", "/swagger-ui/**", "/docs-user/**", "/swagger-resources/**", "/v3/api-docs/**",
			"/h2-user/**"
	};
	
	@Bean
	@Profile("local")
	public SecurityFilterChain localSecurityWebFilterChain(HttpSecurity http) throws Exception {
		return commonSecurityConfig(http.authorizeHttpRequests(a -> a
					.requestMatchers(localDevTool).permitAll()
				))
				.headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.build();
	}

	@Bean
	@Profile("dev")
	public SecurityFilterChain devSecurityWebFilterChain(HttpSecurity http) throws Exception {
		return commonSecurityConfig(http).build();
	}

	private HttpSecurity commonSecurityConfig(HttpSecurity http) throws Exception {
		return http.authorizeHttpRequests(authorize -> authorize
					.requestMatchers(HttpMethod.GET, "/user/**").permitAll()
					.requestMatchers(HttpMethod.POST, "/user/search/**").authenticated()
					.requestMatchers(HttpMethod.DELETE, "/user/**").hasRole("ADMIN")
					.requestMatchers("/user/auth/**").permitAll()
					.anyRequest().authenticated()
				)
				.sessionManagement(session -> session
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				.csrf(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.logout(AbstractHttpConfigurer::disable)
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// cors 설정
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedOrigin("http://localhost:8000"); // 허용할 출처
		configuration.addAllowedOrigin("http://localhost:8001");
		configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
		configuration.addAllowedHeader("*"); // 모든 헤더 허용
		configuration.setAllowCredentials(true); // 인증 정보 포함 허용

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 적용
		return source;
	}

}
