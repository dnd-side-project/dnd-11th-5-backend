package com.odiga.fiesta.user.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.odiga.fiesta.auth.domain.AuthUser;
import com.odiga.fiesta.auth.service.AuthService;
import com.odiga.fiesta.common.BasicResponse;
import com.odiga.fiesta.common.PageResponse;
import com.odiga.fiesta.common.error.ErrorCode;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.festival.dto.response.FestivalInfoWithBookmark;
import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.dto.request.ProfileCreateRequest;
import com.odiga.fiesta.user.dto.request.SocialLoginRequest;
import com.odiga.fiesta.user.dto.response.LoginResponse;
import com.odiga.fiesta.user.dto.response.ProfileCreateResponse;
import com.odiga.fiesta.user.dto.response.TokenReissueResponse;
import com.odiga.fiesta.user.dto.response.UserInfoResponse;
import com.odiga.fiesta.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

	@GetMapping("/me")
	@Operation(summary = "내 정보 조회", description = "로그인한 유저의 정보를 조회합니다.")
	public ResponseEntity<BasicResponse<UserInfoResponse>> getUserInfo(@AuthUser User user) {
		if (user == null) {
			throw new CustomException(ErrorCode.NOT_LOGGED_IN);
		}

		UserInfoResponse response = UserInfoResponse.builder()
			.userId(user.getId())
			.email(user.getEmail())
			.nickname(user.getNickname())
			.statusMessage(user.getStatusMessage())
			.profileImage(user.getProfileImage())
			.isProfileCreated(user.getUserTypeId() != null)
			.userTypeId(user.getUserTypeId())
			.build();

		return ResponseEntity.ok(BasicResponse.ok("내 정보 조회 성공", response));
	}

	@DeleteMapping("/me")
	@Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 진행합니다.")
	public ResponseEntity<BasicResponse<String>> deleteUser(@AuthUser User user) {
		authService.deleteUser(user);
		return ResponseEntity.ok(BasicResponse.ok("회원 탈퇴 성공", null));
	}

	@PostMapping("/profile")
	@Operation(summary = "프로필 생성", description = "프로필을 생성합니다.")
	public ResponseEntity<BasicResponse<ProfileCreateResponse>> createProfile(@AuthUser User user,
		@RequestBody @Valid ProfileCreateRequest request) {

		ProfileCreateResponse response = userService.createProfile(user, request);
		return ResponseEntity.ok(BasicResponse.ok("프로필 생성 성공", response));
	}

	@PostMapping("/reissue")
	@Operation(summary = "JWT 토큰 재발급", description = "기존의 refresh 토큰을 사용하여 새로운 access 토큰과 refresh 토큰을 발급받습니다.")
	public ResponseEntity<BasicResponse<TokenReissueResponse>> reissue(@RequestHeader String refreshToken) {
		TokenReissueResponse tokenResponse = authService.reissue(refreshToken);

		String message = "JWT 토큰 재발급 성공";
		return ResponseEntity.ok(BasicResponse.ok(message, tokenResponse));
	}

	@Operation(
		summary = "유저가 스크랩한 페스티벌 조회",
		description = "유저가 스크랩한 페스티벌을 다건 조회합니다."
	)
	@GetMapping("/bookmarks")
	public ResponseEntity<BasicResponse<PageResponse<FestivalInfoWithBookmark>>> getBookmarkedFestivals(
		@AuthUser User user,
		@ParameterObject @Parameter(description = "Paging parameters", example = "{\"page\":0,\"size\":6,\"sort\":[\"createdAt,desc\"]}")
		@PageableDefault(sort = {"createdAt"}, direction = Sort.Direction.DESC, size = 6) Pageable pageable) {

		Page<FestivalInfoWithBookmark> festivals = userService.getBookmarkedFestivals(user, pageable);
		return ResponseEntity.ok(BasicResponse.ok("스크랩한 페스티벌 조회 성공", PageResponse.of(festivals)));
	}
}
