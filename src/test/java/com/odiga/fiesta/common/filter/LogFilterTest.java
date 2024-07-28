package com.odiga.fiesta.common.filter;

import static org.mockito.BDDMockito.*;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.odiga.fiesta.MockTestSupport;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class LogFilterTest extends MockTestSupport {

	@InjectMocks
	private LogFilter logFilter;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private FilterChain filterChain;

	@DisplayName("http 요청이 오면 doFilter가 정확히 한 번 호출된다.")
	@Test
	void testDoFilterInternal() throws ServletException, IOException {
		// given
		given(request.getRequestURI()).willReturn("/test");
		given(request.getMethod()).willReturn("GET");
		given(response.getStatus()).willReturn(200);

		// when
		logFilter.doFilterInternal(request, response, filterChain);

		// then
		then(filterChain).should(times(1)).doFilter(request, response);
	}
}
