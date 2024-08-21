package com.odiga.fiesta.common.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.odiga.fiesta.common.jwt.JwtAccessDeniedHandler;
import com.odiga.fiesta.common.jwt.JwtAuthenticationEntryPoint;
import com.odiga.fiesta.common.jwt.TokenAuthenticationFilter;
import com.odiga.fiesta.common.jwt.TokenProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final TokenProvider tokenProvider;

	/**
	 * 스프링 시큐리티 기능 비활성화
	 */
	@Bean
	public WebSecurityCustomizer configure() {
		return web -> web.ignoring()
			// 정적 리소스에 대한 스프링 시큐리티 사용을 비활성화
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}

	/**
	 * 특정 HTTP 요청에 대한 웹 기반 보안 구성
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable) //csrf disable
			.formLogin(AbstractHttpConfigurer::disable) //From 로그인 방식 disable
			.httpBasic(AbstractHttpConfigurer::disable) //HTTP Basic 인증 방식 disable

			// 토큰 기반 인증을 사용하기 때문에 세션 기능 비활성화
			.sessionManagement((session) -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

			// 인증, 인가 설정
			.authorizeHttpRequests((auth) -> auth
				.requestMatchers("/**").permitAll()
				.anyRequest().authenticated())

			// cors 설정
			.cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
				CorsConfiguration configuration = new CorsConfiguration();
				configuration.setAllowedOrigins(Arrays.asList(
					"http://localhost:3000",
					"https://odiga.shop",
					"https://fiesta-psi.vercel.app"
				));
				configuration.setAllowedMethods(Collections.singletonList("*"));
				configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
				configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));
				configuration.setAllowCredentials(true);
				configuration.setMaxAge(3600L);
				return configuration;
			}))

			// 헤더를 확인할 커스텀 필터 추가
			.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)

			// 인증 및 인가 예외 처리
			.exceptionHandling(exceptions -> exceptions
				.authenticationEntryPoint(new JwtAuthenticationEntryPoint())
				.accessDeniedHandler(new JwtAccessDeniedHandler())
			);

		return http.build();
	}

	@Bean
	public TokenAuthenticationFilter tokenAuthenticationFilter() {
		return new TokenAuthenticationFilter(tokenProvider);
	}
}
