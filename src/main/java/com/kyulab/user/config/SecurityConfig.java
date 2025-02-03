package com.kyulab.user.config;

import com.kyulab.user.filter.JwtAuthFilter;
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

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthFilter jwtAuthFilter;

	@Bean
	@Profile("local")
	public SecurityFilterChain localSecurityWebFilterChain(HttpSecurity http) throws Exception {
		final String[] localDevTool = {
				"/swagger/**", "/swagger-ui/**", "/docs-user/**", "/swagger-resources/**", "/v3/api-docs/**",
				"/h2-user/**"
		};

		return commonSecurityConfig(http.authorizeHttpRequests(a -> a
					.requestMatchers(localDevTool).permitAll()
				))
				.headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.build();
	}

	@Bean
	@Profile("prod")
	public SecurityFilterChain prodSecurityWebFilterChain(HttpSecurity http) throws Exception {
		return commonSecurityConfig(http)
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.build();
	}

	private HttpSecurity commonSecurityConfig(HttpSecurity http) throws Exception {
		return http.authorizeHttpRequests(authorize -> authorize
					.requestMatchers("/user/auth/**").permitAll()
					.requestMatchers(HttpMethod.GET, "/user/service/**").permitAll()
					.requestMatchers(HttpMethod.POST, "/user/service/**").authenticated()
					.requestMatchers(HttpMethod.DELETE, "/user/**").hasRole("ADMIN")
					.anyRequest().authenticated()
				)
				.sessionManagement(session -> session
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				.csrf(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.logout(AbstractHttpConfigurer::disable)
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of(
				"http://localhost:8000", "http://localhost:8000", "http://localhost:3000", "https://localhost:3000"
		));
		configuration.setAllowedMethods(List.of(
				"GET", "POST", "PUT", "DELETE", "OPTIONS"
		));
		configuration.addAllowedHeader("*"); // 모든 헤더 허용
		configuration.setAllowCredentials(true); // 인증 정보 포함 허용

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 적용
		return source;
	}

}
