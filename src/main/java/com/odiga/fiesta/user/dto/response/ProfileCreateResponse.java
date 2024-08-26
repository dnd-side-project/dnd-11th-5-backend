package com.odiga.fiesta.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProfileCreateResponse {

	@Schema(description = "유저 유형 id", example = "1")
	private Long userTypeId;

	@Schema(description = "유저 유형 명칭", example = "로맨티스트")
	private String userTypeName;

	@Schema(description = "유형에 해당되는 카드 이미지", example = "https://fiesta-image.s3.ap-northeast-2.amazonaws.com/user_type/card/user-explore.png")
	private String userTypeImage;
}
