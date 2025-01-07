package com.kyulab.user.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI apiDocDec() {
		Info info = new Info()
					.title("springdoc-openapi")
					.version("0.0.2")
					.description("user-app");

		SecurityRequirement securityRequirement = new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION);
		Components jwtTestComp = new Components() // 액세스 토큰인증 기능 추가
								.addSecuritySchemes(HttpHeaders.AUTHORIZATION, new SecurityScheme()
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")
								.in(SecurityScheme.In.HEADER)
								.name(HttpHeaders.AUTHORIZATION));
		return new OpenAPI()
				.info(info)
				.components(jwtTestComp)
				.addSecurityItem(securityRequirement);
	}
	
}
