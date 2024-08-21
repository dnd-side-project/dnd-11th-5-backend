package com.odiga.fiesta.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.odiga.fiesta.auth.domain.AuthUser;
import com.odiga.fiesta.auth.service.AuthService;
import com.odiga.fiesta.common.BasicResponse;
import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.dto.request.SocialLoginRequest;
import com.odiga.fiesta.user.dto.request.UserRequest;
import com.odiga.fiesta.user.dto.response.LoginResponse;
import com.odiga.fiesta.user.dto.response.UserResponse;
import com.odiga.fiesta.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "User", description = "USER 관련 API")
public class UserController {

	private final UserService userService;
	private final AuthService authService;

	@PostMapping("/oauth/login")
	@Operation(summary = "소셜 로그인", description = "소셜 로그인을 진행합니다.")
	public ResponseEntity<BasicResponse<LoginResponse>> kakaoLogin(@RequestBody SocialLoginRequest request) {

		User.validateEmail(request.getEmail());
		LoginResponse loginResponse = authService.kakaoLogin(request.getAccessToken());
		return ResponseEntity.ok(BasicResponse.ok("로그인 성공", loginResponse));
	}

	@DeleteMapping("/me")
	@Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 진행합니다.")
	public ResponseEntity<BasicResponse<String>> deleteUser(@AuthUser User user) {
		authService.deleteUser(user);
		return ResponseEntity.ok(BasicResponse.ok("회원 탈퇴 성공", null));
	}

	@PostMapping("/profile")
	@Operation(summary = "프로필 생성", description = "oauthId를 통해 프로필을 생성합니다.")
	public ResponseEntity<BasicResponse<UserResponse.createProfileDTO>> createProfile(@AuthUser User user,
		@RequestBody @Valid UserRequest.createProfileDTO request) {

		UserResponse.createProfileDTO response = userService.createProfile(user, request);
		return ResponseEntity.ok(BasicResponse.ok("프로필 생성 성공", response));
	}

	@PostMapping("/reissue")
	@Operation(summary = "JWT 토큰 재발급", description = "기존의 refresh 토큰을 사용하여 새로운 access 토큰과 refresh 토큰을 발급받습니다.")
	public ResponseEntity<BasicResponse<UserResponse.reissueDTO>> reissue(@RequestHeader String refreshToken) {
		UserResponse.reissueDTO tokenResponse = authService.reissue(refreshToken);

		String message = "JWT 토큰 재발급 성공";
		return ResponseEntity.ok(BasicResponse.ok(message, tokenResponse));
	}
}
