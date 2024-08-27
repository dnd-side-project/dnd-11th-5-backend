package com.odiga.fiesta.review.dto.response;

import com.odiga.fiesta.user.domain.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUserInfo {

	private Long userId;
	private String profileImage;
	private String nickname;

	public static ReviewUserInfo of(User user) {
		return ReviewUserInfo.builder()
			.userId(user.getId())
			.profileImage(user.getProfileImage())
			.nickname(user.getNickname())
			.build();
	}
}
