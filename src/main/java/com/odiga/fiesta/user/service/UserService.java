package com.odiga.fiesta.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.common.jwt.TokenProvider;
import com.odiga.fiesta.common.util.NicknameUtils;
import com.odiga.fiesta.common.util.RedisUtils;
import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.domain.UserType;
import com.odiga.fiesta.user.domain.accounts.OauthUser;
import com.odiga.fiesta.user.domain.mapping.*;
import com.odiga.fiesta.user.domain.oauth.OauthProvider;
import com.odiga.fiesta.user.dto.UserRequest;
import com.odiga.fiesta.user.repository.*;
import com.odiga.fiesta.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static com.odiga.fiesta.common.error.ErrorCode.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final OauthUserRepository oauthUserRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final UserCompanionRepository userCompanionRepository;
    private final UserMoodRepository userMoodRepository;
    private final UserPriorityRepository userPriorityRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;

    private final TokenProvider tokenProvider;

    private final RedisUtils<String> redisUtils;

    private final NicknameUtils nicknameUtils;

    private final UserTypeService userTypeService;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String REDIRECT_URI;

    private static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(2);

    private static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);

    private static final String REFRESH_TOKEN_CATEGORY = "refresh";

    private static final String ACCESS_TOKEN_CATEGORY = "access";



    // 카카오 로그인
    @Transactional
    public UserResponse.loginDTO kakaoLogin(String code) {
        //인가코드로 OAuth2 액세스 토큰 요청
        String oauthAccessToken = getAccessToken(code);

        //OAuth2 액세스 토큰으로 회원 정보 요청
        JsonNode responseJson = getKakaoUserInfo(oauthAccessToken);

        //oauthId 조회
        String oauthIdString = responseJson.get("id").asText();
        Long oauthId = Long.parseLong(oauthIdString);

        String accessToken = null;
        String refreshToken = null;

        //DB에서 회원 정보 조회
        OauthUser user = oauthUserRepository.findByProviderId(oauthId).orElse(null);

        //토큰 발급
        if(user != null) {
            accessToken = issueAccessToken(user);
            refreshToken = issueRefreshToken(user);
        }

        //응답 설정
        return UserResponse.loginDTO.builder()
                .oauthId(oauthId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 프로필 생성
    @Transactional
    public UserResponse.createProfileDTO createProfile(Long oauthId, UserRequest.createProfileDTO request) {

        // 이미 존재하는 회원 정보인지 검사
        if (oauthUserRepository.existsByProviderId(oauthId)) throw new CustomException(ALREADY_JOINED);

        // 유저 유형 도출
        UserType userType = userTypeService.getUserType(request);

        // DB에 OUATH 회원 정보 저장
        OauthUser user = OauthUser.builder()
                .userTypeId(userType.getId())
                .roleId(1L)
                .nickname(nicknameUtils.generateRandomNickname())
                .profileImage(userType.getProfileImage())
                .statusMessage("페스티벌의 시작 피에스타와 함께!")
                .provider(OauthProvider.KAKAO)
                .providerId(oauthId)
                .build();

        oauthUserRepository.save(user);

        // 온보딩 정보 저장
        saveOnBoardingInfo(user.getId(), request);

        // UserRole 저장
        UserRoleId userRoleId = new UserRoleId(user.getId(), 1L);

        UserRole userRole = UserRole.builder()
                .id(userRoleId)
                .build();

        userRoleRepository.save(userRole);

        //토큰 발급
        String accessToken = issueAccessToken(user);
        String refreshToken = issueRefreshToken(user);

        // DTO 반환
        return UserResponse.createProfileDTO.builder()
                .userTypeId(userType.getId())
                .userTypeName(userType.getName())
                .userTypeImage(userType.getProfileImage())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
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

    // AccessToken 생성
    public String issueAccessToken(User user) {
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION, ACCESS_TOKEN_CATEGORY);
        log.info("access: " + accessToken);

        return accessToken;
    }

    // RefreshToken 생성 및 저장
    public String issueRefreshToken(User user) {
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION, REFRESH_TOKEN_CATEGORY);
        log.info("refresh : " + refreshToken);

        redisUtils.setData(user.getId().toString(), refreshToken,REFRESH_TOKEN_DURATION.toMillis());
        return refreshToken;
    }

    /**
     * 인가 코드로 카카오 서버에 액세스 토큰을 요청하는 메서드이다.
     *
     * @param code 인가 코드
     * @return 액세스 토큰
     */
    private String getAccessToken(String code) {

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", CLIENT_ID);
        body.add("redirect_uri", REDIRECT_URI);
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://kauth.kakao.com/oauth/token",
                    HttpMethod.POST,
                    tokenRequest,
                    String.class
            );

            // HTTP 응답에서 액세스 토큰 꺼내기
            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = null;
            try {
                jsonNode = objectMapper.readTree(responseBody);
            } catch (JsonProcessingException e) {
                throw new CustomException(JSON_PARSING_ERROR);
            }
            return jsonNode.get("access_token").asText();

        } catch (HttpClientErrorException e) {
            throw new CustomException(INVALID_CODE);
        }
    }

    /**
     * 액세스 토큰으로 카카오 서버에 회원 정보를 요청하는 메서드이다.
     *
     * @param accessToken 액세스 토큰
     * @return JSON 형식의 회원 정보
     */
    private JsonNode getKakaoUserInfo(String accessToken) {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> userInfoRequest = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.POST,
                    userInfoRequest,
                    String.class
            );

            // HTTP 응답 반환
            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(responseBody);

        } catch (HttpClientErrorException e) { // HTTP 오류 응답 처리
            throw new CustomException(INVALID_TOKEN);
        } catch (JsonProcessingException e) { // JSON 파싱 오류 처리
            throw new CustomException(JSON_PARSING_ERROR);
        }
    }

    // 온보딩 정보 저장
    private void saveOnBoardingInfo(Long userId, UserRequest.createProfileDTO request) {
        List<Long> categories = request.getCategory();
        List<Long> moods = request.getMood();
        List<Long> companions = request.getCompanion();
        List<Long> priorities = request.getPriority();

        // 카테고리 정보 저장
        List<UserCategory> userCategories = categories.stream()
                .map(categoryId -> UserCategory.of(userId, categoryId))
                .collect(Collectors.toList());

        userCategoryRepository.saveAll(userCategories);

        // 분위기 정보 저장
        List<UserMood> userMoods = moods.stream()
                .map(moodId -> UserMood.of(userId, moodId))
                .collect(Collectors.toList());

        userMoodRepository.saveAll(userMoods);

        // 동행유형 정보 저장
        List<UserCompanion> userCompanions = companions.stream()
                .map(companionId -> UserCompanion.of(userId, companionId))
                .collect(Collectors.toList());

        userCompanionRepository.saveAll(userCompanions);

        // 우선순위 정보 저장
        List<UserPriority> userPriorities = priorities.stream()
                .map(priorityId -> UserPriority.of(userId, priorityId))
                .collect(Collectors.toList());

        userPriorityRepository.saveAll(userPriorities);
    }
}
