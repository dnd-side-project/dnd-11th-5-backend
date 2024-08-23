package com.odiga.fiesta.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
				.info(new Info()
						.version("v1.0") // 버전
						.title("Fiesta API") // 이름
						.description("Fiesta API")) // 설명
				.addSecurityItem(new SecurityRequirement()
						.addList("BearerAuth"))
				.components(new io.swagger.v3.oas.models.Components()
						.addSecuritySchemes("BearerAuth", new SecurityScheme()
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")
								.in(SecurityScheme.In.HEADER)
								.name("Authorization")));
	}
}