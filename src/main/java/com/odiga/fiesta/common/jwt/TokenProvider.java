package com.odiga.fiesta.common.jwt;

import static com.odiga.fiesta.common.error.ErrorCode.*;

import java.security.Key;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import com.odiga.fiesta.auth.domain.UserAccount;
import com.odiga.fiesta.common.util.RedisUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.odiga.fiesta.common.error.ErrorCode;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class TokenProvider {

	@Value("${spring.jwt.secret}")
	private String secretKeyString; // secretKey는 String으로 저장됨

	private final Clock clock;

	private final RedisUtils<String> redisUtils;

	private final UserRepository userRepository;

	private Key getSecretKey() {
		return Keys.hmacShaKeyFor(secretKeyString.getBytes()); // Key 객체로 변환
	}

	public String generateToken(User user, Duration expiredAt, String category) {
		LocalDateTime now = LocalDateTime.now(clock);
		Date expiryDate = Date.from(now.plus(expiredAt).atZone(ZoneId.systemDefault()).toInstant());

		return makeToken(expiryDate, user, category);
	}

	/**
	 * JWT 토큰을 생성하는 메서드이다.
	 *
	 * @param expiry 토큰의 만료 시간
	 * @param user   회원 정보
	 * @return 생성된 토큰
	 */
	private String makeToken(Date expiry, User user, String category) {
		Date now = Date.from(LocalDateTime.now(clock).atZone(ZoneId.systemDefault()).toInstant());
		// log.info("Token issued at: {}", now);
		// log.info("Token expires at: {}", expiry);

		return Jwts.builder()
			.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
			.setIssuedAt(now)
			.setExpiration(expiry)
			.setSubject(String.valueOf(user.getId()))
			.claim("id", user.getId())
			.claim("category", category)
			.claim("email", user.getEmail())
			.signWith(getSecretKey(), SignatureAlgorithm.HS256)
			.compact();
	}

	/**
	 * JWT 토큰의 유효성을 검증하는 메서드이다.
	 *
	 * @param token 검증할 JWT 토큰
	 * @return 토큰 유효 여부
	 */
	public void validateToken(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(getSecretKey())
				.build()
				.parseClaimsJws(token);
		} catch (ExpiredJwtException e) {
			// 토큰이 만료되었음을 명시적으로 처리
			log.info("Expired JWT token: {}", e.getMessage());
			throw new CustomException(TOKEN_EXPIRED);
		} catch (Exception e) {
			// 서명 오류, 잘못된 토큰 등 기타 JWT 관련 오류를 처리
			log.info("Invalid JWT token: {}", e.getMessage());
			throw new CustomException(INVALID_TOKEN);
		}
	}

	// Redis에 저장된 리프레시 토큰과 입력된 리프레시 토큰이 일치하는지 판단
	public void validateStoredRefreshToken(Long userId, String refreshToken) {
		String storedToken = redisUtils.getData(userId.toString(), String.class);

		// 저장된 토큰이 null이거나 입력된 토큰과 일치하지 않으면 예외를 던짐
		if (storedToken == null || !storedToken.equals(refreshToken)) {
			throw new CustomException(INVALID_TOKEN);
		}
	}

	/**
	 * JWT 토큰의 만료 시간을 조회하는 메서드이다.
	 *
	 * @param token 검증할 JWT 토큰
	 * @return 토큰 만료 여부
	 */
	public boolean isExpired(String token) {
		Claims claims = getClaims(token);
		return claims.getExpiration().before(new Date());
	}

	/**
	 * JWT 토큰에서 카테고리를 추출하는 메서드이다.
	 *
	 * @param token JWT 토큰
	 * @return 카테고리
	 */
	public String getCategory(String token) {
		Claims claims = getClaims(token);
		return claims.get("category", String.class);
	}

	/**
	 * 토큰에서 회원 ID를 가져오는 메서드이다.
	 *
	 * @param token JWT 토큰
	 * @return 회원 ID
	 */
	public Long getUserId(String token) {
		Claims claims = getClaims(token);
		return claims.get("id", Long.class);
	}

	// 사용자가 ID의 유효성을 검증하는 메서드
	public boolean isValidUserId(Long userId) {
		return userId != null && userId > 0;
	}

	/**
	 * 토큰 기반으로 인증 정보를 가져오는 메서드이다.
	 *
	 * @param token 인증된 회원의 토큰
	 * @return 인증 정보를 담은 Authentication 객체
	 */
	public Authentication getAuthentication(String token) {
		Claims claims = getClaims(token);
		// Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
		UserAccount userAccount = getUserAccountFromToken(token);

		return new UsernamePasswordAuthenticationToken(userAccount, null, userAccount.getAuthorities());
	}

	public UserAccount getUserAccountFromToken(String token) {
		String email = getEmailFromToken(token); // 토큰에서 이메일 추출
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(USER_NOT_FOUND));
		return new UserAccount(user);
	}

	private String getEmailFromToken(String token) {
		Claims claims = getClaims(token);
		return claims.get("email", String.class);
	}

	/**
	 * 주어진 토큰에서 클레임을 조회하는 메서드이다.
	 *
	 * @param token JWT 토큰
	 * @return 클레임 객체
	 */
	private Claims getClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(getSecretKey())
			.build()
			.parseClaimsJws(token)
			.getBody();
	}
}
