package com.odiga.fiesta.user.controller;

import com.odiga.fiesta.common.BasicResponse;
import com.odiga.fiesta.user.dto.UserRequest;
import com.odiga.fiesta.user.service.UserService;
import com.odiga.fiesta.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
    public ResponseEntity<BasicResponse<UserResponse.loginDTO>> kakaoLogin(@RequestHeader @NotNull String code) {
        UserResponse.loginDTO loginResponse = userService.kakaoLogin(code);

        String message = "카카오 로그인 성공";
        return ResponseEntity.ok(BasicResponse.ok(message, loginResponse));
    }

    @PostMapping("/profile")
    @Operation(summary = "프로필 생성", description = "oauthId를 통해 프로필을 생성합니다.")
    public ResponseEntity<BasicResponse<UserResponse.createProfileDTO>> createProfile(@RequestHeader Long oauthId,
                                                                                      @RequestBody  @Valid UserRequest.createProfileDTO request) {
        UserResponse.createProfileDTO response = userService.createProfile(oauthId, request);

        String message = "프로필 생성 성공";
        return ResponseEntity.ok(BasicResponse.ok(message, response));
    }

    @PostMapping("/reissue")
    @Operation(summary = "JWT 토큰 재발급", description = "기존의 refresh 토큰을 사용하여 새로운 access 토큰과 refresh 토큰을 발급받습니다.")
    public ResponseEntity<BasicResponse<UserResponse.reissueDTO>> reissue(@RequestHeader String refreshToken) {
        UserResponse.reissueDTO tokenResponse = userService.reissue(refreshToken);

        String message = "JWT 토큰 재발급 성공";
        return ResponseEntity.ok(BasicResponse.ok(message, tokenResponse));
    }
}
