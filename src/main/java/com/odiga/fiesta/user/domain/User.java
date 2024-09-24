package com.odiga.fiesta.user.domain;

import static com.odiga.fiesta.common.error.ErrorCode.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.odiga.fiesta.common.domain.BaseEntity;
import com.odiga.fiesta.common.error.exception.CustomException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "users")
@ToString
public class User extends BaseEntity {

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-z0-9._-]+@[a-z]+[.]+[a-z]{2,3}$");

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Column(name = "email", length = 320, unique = true)
	private String email;

	@Column(name = "user_type_id")
	private Long userTypeId;

	@Column(nullable = false, length = 10)
	private String nickname;

	@Column(name = "status_message", length = 30)
	private String statusMessage;

	@Column(name = "profile_image")
	private String profileImage;

	@Builder
	public User(Long id, String email, Long userTypeId, String nickname, String statusMessage, String profileImage) {

		this.id = id;
		this.email = email;
		this.userTypeId = userTypeId;
		this.nickname = nickname;
		this.statusMessage = statusMessage;
		this.profileImage = profileImage;
	}

	public static void validateEmail(final String email) {
		Matcher matcher = EMAIL_PATTERN.matcher(email);
		if (!matcher.matches()) {
			throw new CustomException(INVALID_EMAIL);
		}
	}

	public static void validateNickname(final String nickname) {
		if (nickname.isEmpty() || nickname.length() > 10) {
			throw new CustomException(INVALID_NICKNAME_LENGTH);
		}
	}

	public static void validateStatusMessage(final String statusMessage) {
		if (statusMessage.isEmpty() || statusMessage.length() > 30) {
			throw new CustomException(INVALID_STATUS_MESSAGE_LENGTH);
		}
	}

	public void updateUserType(final Long userTypeId) {
		this.userTypeId = userTypeId;
	}

	public void updateUserInfo(String nickname, String statusMessage) {
		validateNickname(nickname);
		validateStatusMessage(statusMessage);
		this.nickname = nickname;
		this.statusMessage = statusMessage;
	}
}
