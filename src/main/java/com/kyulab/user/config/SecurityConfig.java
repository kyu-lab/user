package com.kyulab.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	@Profile("local")
	public SecurityFilterChain localSecurityWebFilterChain(HttpSecurity http) throws Exception {
		return http.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(HttpMethod.GET, "/api/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/user/**").permitAll()
				.requestMatchers("/swagger/**", "/swagger-ui/**", "/docs-local/**", "/swagger-resources/**", "/v3/api-docs/**", "/h2-user/**").permitAll()
				.requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
				.anyRequest().authenticated()
			)
			.csrf(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.headers(header -> // 동일 출처 iframe 허용 (h2)
				header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
			)
			.build();
	}

	@Bean
	@Profile("dev")
	public SecurityFilterChain devSecurityWebFilterChain(HttpSecurity http) throws Exception {
		return http.authorizeHttpRequests(authorize -> authorize
					.requestMatchers(HttpMethod.GET, "/api/**").permitAll()
					.requestMatchers(HttpMethod.POST, "/api/user/**").permitAll()
					.requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
					.anyRequest().authenticated()
			)
			.csrf(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
