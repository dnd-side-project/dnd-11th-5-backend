package com.odiga.fiesta.auth.service;

import static com.odiga.fiesta.auth.dto.KakaoProfile.*;
import static com.odiga.fiesta.common.error.ErrorCode.*;
import static java.util.Objects.*;
import static org.springframework.http.MediaType.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.odiga.fiesta.auth.domain.Authority;
import com.odiga.fiesta.auth.dto.KakaoProfile;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.common.jwt.TokenProvider;
import com.odiga.fiesta.common.util.HttpClientUtil;
import com.odiga.fiesta.common.util.RedisUtils;
import com.odiga.fiesta.user.domain.Role;
import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.domain.mapping.UserRole;
import com.odiga.fiesta.user.dto.response.LoginResponse;
import com.odiga.fiesta.user.dto.response.UserResponse;
import com.odiga.fiesta.user.repository.RoleRepository;
import com.odiga.fiesta.user.repository.UserRepository;
import com.odiga.fiesta.user.repository.UserRoleRepository;
import com.odiga.fiesta.user.util.NicknameUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
	private final UserRoleRepository userRoleRepository;

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;

	private final TokenProvider tokenProvider;
	private final NicknameUtils nicknameUtils;
	private final RedisUtils redisUtils;

	private static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(2);
	private static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
	private static final String REFRESH_TOKEN_CATEGORY = "refresh";
	private static final String ACCESS_TOKEN_CATEGORY = "access";
	private static final String DEFAULT_STATUS_MESSAGE = "페스티벌의 시작 피에스타와 함께!";
	private static final String DEFAULT_PROFILE_IMAGE = "https://fiesta-image.s3.ap-northeast-2.amazonaws.com/user_type/user_type_1.png";

	// 카카오 로그인
	@Transactional
	public LoginResponse kakaoLogin(String accessTokenByClient) {

		//OAuth2 액세스 토큰으로 회원 정보 요청
		KakaoProfile response = getKakaoProfile(accessTokenByClient);

		log.info("response (in service): {}", response);
		//oauthId 조회
		KakaoAccount kakaoAccount = response.getKakaoAccount();

		validateKakoAcccount(kakaoAccount);

		log.warn("kakaoAccount: {}", kakaoAccount);

		String email = kakaoAccount.getEmail();

		// 유저 검증 -> 서비스에 존재하지 않는 유저라면 새로운 사용자를 생성한다.
		User user = userRepository.findByEmail(email)
			.orElseGet(() -> saveUser(email));

		// 토큰 발급
		String accessToken = issueAccessToken(user);
		String refreshToken = issueRefreshToken(user);
		boolean isProfileRegistered = !isNull(user.getUserTypeId());

		return LoginResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.isProfileRegistered(isProfileRegistered)
			.build();
	}

	private static void validateKakoAcccount(KakaoAccount kakaoAccount) {
		if (!kakaoAccount.isHasEmail()) {
			throw new CustomException(CAN_NOT_FIND_KAKAO_USER);
		}
	}

	// JWT 토큰 재발급
	@Transactional
	public UserResponse.reissueDTO reissue(String refreshToken) {

		// 유효 검사
		tokenProvider.validateToken(refreshToken);

		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		String category = tokenProvider.getCategory(refreshToken);
		if (!REFRESH_TOKEN_CATEGORY.equals(category)) {
			log.info("category: {}", category);
			throw new CustomException(INVALID_TOKEN);
		}

		//토큰에서 유저 조회
		Long userId = tokenProvider.getUserId(refreshToken);

		if (!tokenProvider.isValidUserId(userId)) {
			throw new CustomException(USER_NOT_FOUND);
		}

		//DB에서 리프레시 토큰 확인
		tokenProvider.validateStoredRefreshToken(userId, refreshToken);

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(USER_NOT_FOUND));

		String newAccess = issueAccessToken(user);
		String newRefresh = issueRefreshToken(user);

		return new UserResponse.reissueDTO(newAccess, newRefresh);
	}

	private User saveUser(String email) {
		Role role = roleRepository.findByAuthority(Authority.ROLE_USER)
			.orElseThrow(() -> new CustomException(ROLE_NOT_FOUND));

		User user = User.builder()
			.nickname(nicknameUtils.generateRandomNickname())
			.statusMessage(DEFAULT_STATUS_MESSAGE)
			.profileImage(DEFAULT_PROFILE_IMAGE)
			.email(email)
			.build();

		User savedUser = userRepository.save(user);

		saveUserRole(role, savedUser);

		return savedUser;
	}

	private UserRole saveUserRole(Role role, User savedUser) {
		UserRole userRole = UserRole.builder()
			.roleId(role.getId())
			.userId(savedUser.getId())
			.build();

		return userRoleRepository.save(userRole);
	}

	// AccessToken 생성
	public String issueAccessToken(User user) {
		return tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION, ACCESS_TOKEN_CATEGORY);
	}

	// RefreshToken 생성 및 저장
	public String issueRefreshToken(User user) {
		String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION, REFRESH_TOKEN_CATEGORY);
		redisUtils.setData(user.getId().toString(), refreshToken, REFRESH_TOKEN_DURATION.toMillis());
		return refreshToken;
	}

	public KakaoProfile getKakaoProfile(String accessToken) {
		String url = "https://kapi.kakao.com/v2/user/me";
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		headers.setContentType(APPLICATION_FORM_URLENCODED);

		// MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

		return HttpClientUtil.sendRequest(url, HttpMethod.POST, headers, null, KakaoProfile.class);
	}
}
