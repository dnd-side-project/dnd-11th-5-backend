package com.odiga.fiesta.user.UserService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.common.jwt.TokenProvider;
import com.odiga.fiesta.common.util.RedisUtils;
import com.odiga.fiesta.user.domain.accounts.OauthUser;
import com.odiga.fiesta.user.repository.OauthUserRepository;
import com.odiga.fiesta.user.dto.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import java.util.Optional;

import static com.odiga.fiesta.common.error.ErrorCode.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final OauthUserRepository oauthUserRepository;

    private final TokenProvider tokenProvider;

    private final RedisUtils redisUtils;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String REDIRECT_URI;

    @Transactional
    public UserResponse.loginDTO kakaoLogin(String code, HttpServletRequest request, HttpServletResponse response) {
        //인가코드로 OAuth2 액세스 토큰 요청
        String oauthAccessToken = getAccessToken(code);

        //OAuth2 액세스 토큰으로 회원 정보 요청
        JsonNode responseJson = getKakaoUserInfo(oauthAccessToken);

        //oauthId 조회
        String oauthIdString = responseJson.get("id").asText();
        Long oauthId = Long.parseLong(oauthIdString);

        String accessToken = null;
        String refreshToken = null;

        Optional<OauthUser> optionalUser = oauthUserRepository.findByProviderId(oauthId);

        //DB에 회원정보가 있을때 토큰 발급
        if (optionalUser.isPresent()) {
            OauthUser user = optionalUser.get();

            //토큰 생성
            accessToken = tokenProvider.generateToken(user, Duration.ofHours(2), "access");
            refreshToken = tokenProvider.generateToken(user, Duration.ofDays(14), "refresh");
            log.info("access: " + accessToken);
            log.info("refresh : " + refreshToken);

            //Refresh 토큰 저장
            redisUtils.setData(user.getId().toString(), refreshToken, Duration.ofDays(14).toMillis());
        }

        //응답 설정
        return UserResponse.loginDTO.builder()
                .oauthId(oauthId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
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
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

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
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

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
}
