package com.odiga.fiesta.common.filter;

import java.io.IOException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LogFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(
		final @NonNull HttpServletRequest request,
		final @NonNull HttpServletResponse response,
		final @NonNull FilterChain filterChain
	) throws ServletException, IOException {
		long requestTime = System.currentTimeMillis();

		filterChain.doFilter(request, response);

		long responseTime = System.currentTimeMillis();

		log(request, response, responseTime - requestTime);
	}

	private void log(
		final @NonNull HttpServletRequest request,
		final @NonNull HttpServletResponse response,
		final long duration
	) {
		String requestURI = request.getRequestURI();
		String requestMethod = request.getMethod();
		int responseStatus = response.getStatus();

		log.info("request URI: {}, request method: {}, response status: {}, duration: {} ms",
			requestURI, requestMethod, responseStatus, duration);
	}
}
