package com.odiga.fiesta.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.odiga.fiesta.common.error.ErrorCode;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.user.UserService.UserService;
import com.odiga.fiesta.user.domain.accounts.OauthUser;
import com.odiga.fiesta.user.domain.oauth.OauthProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.repository.OauthUserRepository;
import com.odiga.fiesta.user.dto.UserResponse;
import com.odiga.fiesta.common.jwt.TokenProvider;
import com.odiga.fiesta.common.util.RedisUtils;

import java.util.Optional;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Transactional
class UserServiceTest {

    @MockBean
    private OauthUserRepository oauthUserRepository;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private RedisUtils redisUtils;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(oauthUserRepository, tokenProvider, redisUtils);
    }

    @DisplayName("카카오 로그인 - 인가 코드 유효하지 않을 때")
    @Test
    void kakaoLogin_invalidCode() {
        // given
        String invalidCode = "invalid-code";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Mocking repository and token provider
        when(oauthUserRepository.findByProviderId(anyLong())).thenThrow(new CustomException(ErrorCode.INVALID_CODE));

        // when & then
        assertThatThrownBy(() -> userService.kakaoLogin(invalidCode, request, response))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("유효하지 않은 인가코드입니다.");
    }
}