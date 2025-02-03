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
				.build();
	}

	@Bean
	@Profile("prod")
	public SecurityFilterChain prodSecurityWebFilterChain(HttpSecurity http) throws Exception {
		return commonSecurityConfig(http)
				.build();
	}

	private HttpSecurity commonSecurityConfig(HttpSecurity http) throws Exception {
		return http.authorizeHttpRequests(authorize -> authorize
					.requestMatchers(HttpMethod.GET, "/api/user/**").permitAll()
					.requestMatchers(HttpMethod.POST, "/api/user/**").permitAll()
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

}
