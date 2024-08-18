package com.odiga.fiesta.user.service;

import com.odiga.fiesta.common.error.ErrorCode;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.user.domain.UserType;
import com.odiga.fiesta.user.dto.UserRequest;
import com.odiga.fiesta.user.repository.UserTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserTypeService {

    private final UserTypeRepository userTypeRepository;

    // 유저 유형 도출
    public UserType getUserType(UserRequest.createProfileDTO request) {

        List<Long> categories = request.getCategory();
        List<Long> moods = request.getMood();

        int romanticScore = getRomanticScore(categories, moods);
        int partyPeopleScore = getPartyPeopleScore(categories, moods);
        int inspireScore = getInspireScore(categories,moods);
        int healingScore = getHealingScore(categories, moods);
        int exploreScore = getExploreScore(categories, moods);

        int maxScore = Math.max(Math.max(romanticScore, partyPeopleScore),
                Math.max(Math.max(inspireScore, healingScore), exploreScore));

        String userTypeName;
        if (maxScore == romanticScore) {
            userTypeName = "로맨티스트";
        } else if (maxScore == partyPeopleScore) {
            userTypeName = "파티피플러";
        } else if (maxScore == inspireScore) {
            userTypeName = "인스파이어러";
        } else if (maxScore == healingScore) {
            userTypeName = "몽글몽글 힐링러";
        } else {
            userTypeName = "탐험러";
        }

        Optional<UserType> userTypeOptional = userTypeRepository.findByName(userTypeName);

        return userTypeOptional.orElseThrow(() -> new CustomException(ErrorCode.USER_TYPE_NOT_FOUND));
    }

    // 로맨티스트 점수 계산
    private int getRomanticScore(List<Long> categories, List<Long> moods) {
        Map<Long, Integer> categoryScores = new HashMap<>();
        categoryScores.put(1L, 4); // 문화
        categoryScores.put(2L, 5); // 음악&댄스
        categoryScores.put(3L, 4); // 영화
        categoryScores.put(4L, 1); // 음식&술
        categoryScores.put(5L, 1); // 액티비티
        categoryScores.put(6L, 4); // 미술
        categoryScores.put(7L, 2); // 역사
        categoryScores.put(8L, 3); // 자연
        categoryScores.put(9L, 1); // 반려동물
        categoryScores.put(10L, 5); // 야간
        categoryScores.put(11L, 5); // 불꽃축제
        categoryScores.put(12L, 3); // 이색축제

        // 무드 점수 매핑
        Map<Long, Integer> moodScores = new HashMap<>();
        moodScores.put(1L, 5); // 낭만적인
        moodScores.put(2L, 5); // 여유로운
        moodScores.put(3L, 3); // 활기찬
        moodScores.put(4L, 1); // 모험적인
        moodScores.put(5L, 4); // 화려한
        moodScores.put(6L, 4); // 예술적인
        moodScores.put(7L, 2); // 힙한
        moodScores.put(8L, 5); // 감성적인
        moodScores.put(9L, 3); // 레트로한
        moodScores.put(10L, 2); // 친근한
        moodScores.put(11L, 3); // 색다른
        moodScores.put(12L, 5); // 로맨틱한
        moodScores.put(13L, 3); // 클래식한
        moodScores.put(14L, 2); // 신비한
        moodScores.put(15L, 2); // 재미있는
        moodScores.put(16L, 4); // 잔잔한
        moodScores.put(17L, 1); // 전통적인
        moodScores.put(18L, 5); // 감동이 있는

        int score = 0;

        // 카테고리 점수 계산
        for (Long categoryId : categories) {
            score += categoryScores.getOrDefault(categoryId, 0);
        }

        // 무드 점수 계산
        for (Long moodId : moods) {
            score += moodScores.getOrDefault(moodId, 0);
        }

        return score;
    }

    // 파티피플러 점수 계산
    private int getPartyPeopleScore(List<Long> categories, List<Long> moods) {
        Map<Long, Integer> categoryScores = new HashMap<>();
        categoryScores.put(1L, 4); // 문화
        categoryScores.put(2L, 5); // 음악&댄스
        categoryScores.put(3L, 4); // 영화
        categoryScores.put(4L, 5); // 음식&술
        categoryScores.put(5L, 5); // 액티비티
        categoryScores.put(6L, 3); // 미술
        categoryScores.put(7L, 1); // 역사
        categoryScores.put(8L, 1); // 자연
        categoryScores.put(9L, 2); // 반려동물
        categoryScores.put(10L, 5); // 야간
        categoryScores.put(11L, 5); // 불꽃축제
        categoryScores.put(12L, 4); // 이색축제

        // 무드 점수 매핑
        Map<Long, Integer> moodScores = new HashMap<>();
        moodScores.put(1L, 5); // 낭만적인
        moodScores.put(2L, 2); // 여유로운
        moodScores.put(3L, 5); // 활기찬
        moodScores.put(4L, 4); // 모험적인
        moodScores.put(5L, 4); // 화려한
        moodScores.put(6L, 4); // 예술적인
        moodScores.put(7L, 5); // 힙한
        moodScores.put(8L, 2); // 감성적인
        moodScores.put(9L, 3); // 레트로한
        moodScores.put(10L, 3); // 친근한
        moodScores.put(11L, 4); // 색다른
        moodScores.put(12L, 3); // 로맨틱한
        moodScores.put(13L, 3); // 클래식한
        moodScores.put(14L, 4); // 신비한
        moodScores.put(15L, 5); // 재미있는
        moodScores.put(16L, 1); // 잔잔한
        moodScores.put(17L, 1); // 전통적인
        moodScores.put(18L, 2); // 감동이 있는

        int score = 0;

        // 카테고리 점수 계산
        for (Long categoryId : categories) {
            score += categoryScores.getOrDefault(categoryId, 0);
        }

        // 무드 점수 계산
        for (Long moodId : moods) {
            score += moodScores.getOrDefault(moodId, 0);
        }

        return score;
    }

    // 인스파이어러 점수 계산
    private int getInspireScore(List<Long> categories, List<Long> moods) {
        Map<Long, Integer> categoryScores = new HashMap<>();
        categoryScores.put(1L, 4); // 문화
        categoryScores.put(2L, 5); // 음악&댄스
        categoryScores.put(3L, 5); // 영화
        categoryScores.put(4L, 4); // 음식&술
        categoryScores.put(5L, 4); // 액티비티
        categoryScores.put(6L, 5); // 미술
        categoryScores.put(7L, 2); // 역사
        categoryScores.put(8L, 3); // 자연
        categoryScores.put(9L, 1); // 반려동물
        categoryScores.put(10L, 3); // 야간
        categoryScores.put(11L, 4); // 불꽃축제
        categoryScores.put(12L, 5); // 이색축제

        // 무드 점수 매핑
        Map<Long, Integer> moodScores = new HashMap<>();
        moodScores.put(1L, 5); // 낭만적인
        moodScores.put(2L, 5); // 여유로운
        moodScores.put(3L, 4); // 활기찬
        moodScores.put(4L, 4); // 모험적인
        moodScores.put(5L, 4); // 화려한
        moodScores.put(6L, 5); // 예술적인
        moodScores.put(7L, 4); // 힙한
        moodScores.put(8L, 5); // 감성적인
        moodScores.put(9L, 3); // 레트로한
        moodScores.put(10L, 2); // 친근한
        moodScores.put(11L, 5); // 색다른
        moodScores.put(12L, 5); // 로맨틱한
        moodScores.put(13L, 3); // 클래식한
        moodScores.put(14L, 4); // 신비한
        moodScores.put(15L, 3); // 재미있는
        moodScores.put(16L, 4); // 잔잔한
        moodScores.put(17L, 2); // 전통적인
        moodScores.put(18L, 3); // 감동이 있는

        int score = 0;

        // 카테고리 점수 계산
        for (Long categoryId : categories) {
            score += categoryScores.getOrDefault(categoryId, 0);
        }

        // 무드 점수 계산
        for (Long moodId : moods) {
            score += moodScores.getOrDefault(moodId, 0);
        }

        return score;
    }

    // 몽글몽글 힐링러 점수 계산
    private int getHealingScore(List<Long> categories, List<Long> moods) {
        Map<Long, Integer> categoryScores = new HashMap<>();
        categoryScores.put(1L, 3); // 문화
        categoryScores.put(2L, 2); // 음악&댄스
        categoryScores.put(3L, 5); // 영화
        categoryScores.put(4L, 1); // 음식&술
        categoryScores.put(5L, 1); // 액티비티
        categoryScores.put(6L, 5); // 미술
        categoryScores.put(7L, 4); // 역사
        categoryScores.put(8L, 5); // 자연
        categoryScores.put(9L, 4); // 반려동물
        categoryScores.put(10L, 5); // 야간
        categoryScores.put(11L, 4); // 불꽃축제
        categoryScores.put(12L, 1); // 이색축제

        // 무드 점수 매핑
        Map<Long, Integer> moodScores = new HashMap<>();
        moodScores.put(1L, 5); // 낭만적인
        moodScores.put(2L, 5); // 여유로운
        moodScores.put(3L, 3); // 활기찬
        moodScores.put(4L, 2); // 모험적인
        moodScores.put(5L, 1); // 화려한
        moodScores.put(6L, 4); // 예술적인
        moodScores.put(7L, 1); // 힙한
        moodScores.put(8L, 5); // 감성적인
        moodScores.put(9L, 3); // 레트로한
        moodScores.put(10L, 4); // 친근한
        moodScores.put(11L, 3); // 색다른
        moodScores.put(12L, 5); // 로맨틱한
        moodScores.put(13L, 3); // 클래식한
        moodScores.put(14L, 4); // 신비한
        moodScores.put(15L, 2); // 재미있는
        moodScores.put(16L, 5); // 잔잔한
        moodScores.put(17L, 2); // 전통적인
        moodScores.put(18L, 4); // 감동이 있는

        int score = 0;

        // 카테고리 점수 계산
        for (Long categoryId : categories) {
            score += categoryScores.getOrDefault(categoryId, 0);
        }

        // 무드 점수 계산
        for (Long moodId : moods) {
            score += moodScores.getOrDefault(moodId, 0);
        }

        return score;
    }

    // 탐험러 점수 계산
    private int getExploreScore(List<Long> categories, List<Long> moods) {
        Map<Long, Integer> categoryScores = new HashMap<>();
        categoryScores.put(1L, 5); // 문화
        categoryScores.put(2L, 3); // 음악&댄스
        categoryScores.put(3L, 2); // 영화
        categoryScores.put(4L, 5); // 음식&술
        categoryScores.put(5L, 5); // 액티비티
        categoryScores.put(6L, 2); // 미술
        categoryScores.put(7L, 5); // 역사
        categoryScores.put(8L, 5); // 자연
        categoryScores.put(9L, 1); // 반려동물
        categoryScores.put(10L, 4); // 야간
        categoryScores.put(11L, 3); // 불꽃축제
        categoryScores.put(12L, 4); // 이색축제

        // 무드 점수 매핑
        Map<Long, Integer> moodScores = new HashMap<>();
        moodScores.put(1L, 4); // 낭만적인
        moodScores.put(2L, 3); // 여유로운
        moodScores.put(3L, 5); // 활기찬
        moodScores.put(4L, 5); // 모험적인
        moodScores.put(5L, 2); // 화려한
        moodScores.put(6L, 4); // 예술적인
        moodScores.put(7L, 4); // 힙한
        moodScores.put(8L, 2); // 감성적인
        moodScores.put(9L, 3); // 레트로한
        moodScores.put(10L, 3); // 친근한
        moodScores.put(11L, 5); // 색다른
        moodScores.put(12L, 1); // 로맨틱한
        moodScores.put(13L, 3); // 클래식한
        moodScores.put(14L, 5); // 신비한
        moodScores.put(15L, 4); // 재미있는
        moodScores.put(16L, 1); // 잔잔한
        moodScores.put(17L, 5); // 전통적인
        moodScores.put(18L, 4); // 감동이 있는

        int score = 0;

        // 카테고리 점수 계산
        for (Long categoryId : categories) {
            score += categoryScores.getOrDefault(categoryId, 0);
        }

        // 무드 점수 계산
        for (Long moodId : moods) {
            score += moodScores.getOrDefault(moodId, 0);
        }

        return score;
    }
}
