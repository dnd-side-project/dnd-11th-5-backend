package com.odiga.fiesta.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odiga.fiesta.common.error.ErrorCode;
import com.odiga.fiesta.common.error.exception.CustomException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 요청 헤더의 Authorization 키의 값 조회
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);

        // 가져온 값에서 접두사 제거
        String token = getAccessToken(authorizationHeader);

        // 토큰이 없다면 다음 필터로 넘김(권한없는 요청일 수도 있으니)
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 유효성 검증
            tokenProvider.validateToken(token);

            // 유효한 토큰인 경우 인증 정보 설정
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (CustomException e) {
            sendErrorResponse(response, e.getErrorCode());
            return;
        }

        // 토큰이 access인지 확인(발급시 페이로드에 명시), 만료시 다음 필터로 넘기지 않음
        String category = tokenProvider.getCategory(token);
        if (!"access".equals(category)) {
            System.out.println("category: " + category);
            sendErrorResponse(response, ErrorCode.DIFFERENT_CATEGORY);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(errorCode.getStatus());

        // JSON 객체 생성
        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("statusCode", errorCode.getStatus());
        errorResponse.put("code", errorCode.getCode());
        errorResponse.put("message", errorCode.getMessage());

        // JSON 변환기 생성
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        // 응답 작성
        PrintWriter writer = response.getWriter();
        writer.print(jsonResponse);
        writer.flush();
    }
}

