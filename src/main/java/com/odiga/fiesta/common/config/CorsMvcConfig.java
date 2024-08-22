package com.odiga.fiesta.common.config;

import static org.springframework.http.HttpMethod.*;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry corsRegistry) {
		corsRegistry.addMapping("/api/v1/**")
			.allowedOrigins("http://localhost:3000", "https://odiga.shop", "https://fiesta-psi.vercel.app")
			.allowedMethods(
				GET.name(),
				HEAD.name(),
				POST.name(),
				PUT.name(),
				DELETE.name(),
				PATCH.name(),
				OPTIONS.name())
			.allowCredentials(true);
	}
}
