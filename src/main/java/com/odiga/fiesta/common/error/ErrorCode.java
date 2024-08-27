package com.odiga.fiesta.common.error;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;

@Getter
public enum ErrorCode {

	//Internal Server Error
	INTERNAL_SERVER_ERROR(500, "S001", "서버에 문제가 생겼습니다."),
	UPLOAD_FAIL(500, "S002", "파일 업로드에 실패하였습니다."),
	JSON_PARSING_ERROR(500, "S003", "JSON 파싱 오류가 발생했습니다."),
	USER_TYPE_NOT_FOUND(500, "S004", "유저 유형 도출에 실패했습니다."),

	// Client Error
	METHOD_NOT_ALLOWED(405, "C001", "적절하지 않은 HTTP 메소드입니다."),
	INVALID_TYPE_VALUE(400, "C002", "요청 값의 타입이 잘못되었습니다."),
	INVALID_INPUT_VALUE(400, "C003", "적절하지 않은 값입니다."),
	NOT_FOUND(404, "C004", "해당 리소스를 찾을 수 없습니다."),
	BAD_REQUEST(400, "C005", "잘못된 요청입니다."),
	INVALID_LENGTH(400, "C006", "올바르지 않은 길이입니다."),
	INVALID_IMAGE_TYPE(400, "C007", "지원하지 않는 이미지 형식입니다."),
	INVALID_EXTENSION_TYPE(400, "C008", "파일의 확장자가 잘못되었습니다."),
	MISSING_REQUEST_PARAMETER(400, "C009", "필수 파라미터가 누락되었습니다."),
	UNAUTHENTICATED_USER(401, "C010", "인증되지 않은 사용자입니다."),
	NOT_LOGGED_IN(401, "C011", "로그인이 필요합니다."),
	HTTP_MEDIA_TYPE_NOT_SUPPORTED(415, "C012", "지원하지 않는 미디어 타입입니다."),

	// Domain

	// User
	USER_NOT_FOUND(403, "U001", "사용자의 정보를 찾을 수 없습니다."),
	INVALID_CODE(400, "U002", "유효하지 않은 인가코드입니다."),
	INVALID_TOKEN(401, "U003", "유효하지 않은 토큰입니다."),
	TOKEN_EXPIRED(401, "U004", "만료된 토큰입니다."),
	ALREADY_JOINED(409, "U005", "이미 존재하는 유저입니다."),
	INVALID_USER_TYPE(400, "U006", "유효하지 않은 유저 타입입니다."),
	INVALID_EMAIL(400, "U007", "유효하지 않은 이메일입니다."),
	INVALID_NICKNAME_LENGTH(400, "U008", "닉네임은 1자 이상 10자 이하여야 합니다."),
	CAN_NOT_FIND_KAKAO_USER(400, "U009", "카카오 사용자를 찾을 수 없습니다."),
	ROLE_NOT_FOUND(404, "U010", "존재하지 않는 권한입니다."),

	// Festival
	INVALID_FESTIVAL_MONTH(400, "F001", "입력된 월이 유효하지 않습니다. 월은 1월부터 12월 사이여야 합니다."),
	INVALID_FESTIVAL_DATE(400, "F002", "유효하지 않은 날짜입니다."),
	FESTIVAL_AREA_NOT_FOUND(400, "F003", "존재하지 않는 지역 id 입니다."),
	FESTIVAL_CATEGORY_NOT_FOUND(400, "F004", "존재하지 않는 페스티벌 카테고리 입니다."),
	INVALID_CURRENT_LOCATION(400, "F005", "현재 위치 값을 알 수 없습니다."),
	FESTIVAL_NOT_FOUND(400, "F006", "페스티벌의 정보를 찾을 수 없습니다."),
	QUERY_CANNOT_BE_EMPTY(400, "F007", "검색어는 필수입니다."),
	QUERY_CANNOT_BE_BLANK(400, "F008", "공백으로는 검색할 수 없습니다."),
	FESTIVAL_IS_PENDING(400, "F009", "승인되지 않은 페스티벌입니다."),
	INVALID_SIDO_NAME(400, "F010", "유효하지 않은 시도 이름입니다."),
	FESTIAL_IMAGE_EXCEEDED(400, "F011", "페스티벌 이미지는 최대 3개까지 업로드 가능합니다."),
	PRIORITY_NOT_FOUND(404, "F012", "존재하지 않는 우선순위입니다."),
	CATEGORY_NOT_FOUND(404, "F013", "존재하지 않는 카테고리입니다."),
	COMPANION_NOT_FOUND(404, "F014", "존재하지 않는 일행 유형입니다."),
	MOOD_NOT_FOUND(404, "F015", "존재하지 않는 분위기입니다."),

    // LOG
    LOG_NOT_FOUND(404, "L001", "존재하지 않는 방문일지입니다."),
    LOG_IMAGE_COUNT_EXCEEDED(400, "L002", "이미지는 최대 3개까지 업로드 가능합니다."),

    // Review
    REVIEW_NOT_FOUND(404, "R001", "존재하지 않는 리뷰입니다."),
	INVALID_REVIEW_SORT_TYPE(400, "R002", "유효하지 않은 리뷰 정렬 타입입니다."),

    // Keyword
    KEYWORD_NOT_FOUND(404, "K001", "존재하지 않는 키워드입니다.");

	private final int status;
	private final String code;
	private final String message;

	ErrorCode(int status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}

	// 메시지 기반으로 에러코드를 조회한다.
	private static final Map<String, ErrorCode> messageMap
		= Collections.unmodifiableMap(Stream.of(values())
		.collect(Collectors.toMap(ErrorCode::getMessage, Function.identity())));

	public static ErrorCode fromMessage(String message) {
		return messageMap.get(message);
	}
}
