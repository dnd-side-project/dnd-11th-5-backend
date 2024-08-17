package com.odiga.fiesta.user.controller;

import com.odiga.fiesta.common.BasicResponse;
import com.odiga.fiesta.user.service.UserService;
import com.odiga.fiesta.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "User", description = "USER 관련 API")
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "카카오 로그인", description = "카카오 인가코드를 통해 로그인 후 JWT 토큰을 생성합니다.")
    public ResponseEntity<BasicResponse<UserResponse.loginDTO>> kakaoLogin(@RequestHeader String code, HttpServletRequest request, HttpServletResponse response) {
        UserResponse.loginDTO loginResponse = userService.kakaoLogin(code, request, response);

        String message = "카카오 로그인 성공";
        return ResponseEntity.ok(BasicResponse.ok(message, loginResponse));
    }
}
