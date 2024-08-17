package com.odiga.fiesta.common.error;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    //Internal Server Error
    INTERNAL_SERVER_ERROR(500, "S001", "서버에 문제가 생겼습니다."),
    UPLOAD_FAIL(500, "S002", "파일 업로드에 실패하였습니다."),
    JSON_PARSING_ERROR(500, "S002", "JSON 파싱 오류가 발생했습니다."),

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

    // Domain

    // Festival
    INVALID_FESTIVAL_MONTH(400, "F001", "입력된 월이 유효하지 않습니다. 월은 1월부터 12월 사이여야 합니다."),
    INVALID_FESTIVAL_DATE(400, "F002", "유효하지 않은 날짜입니다."),
    FESTIVAL_AREA_NOT_FOUND(400, "F003", "존재하지 않는 지역 id 입니다."),
    FESTIVAL_CATEGORY_NOT_FOUND(400, "F004", "존재하지 않는 페스티벌 카테고리 입니다."),
    INVALID_CURRENT_LOCATION(400, "F005", "현재 위치 값을 알 수 없습니다."),

    // LOG
    LOG_NOT_FOUND(404, "L001", "존재하지 않는 방문일지입니다."),

    // USER
    INVALID_CODE(400, "U001", "유효하지 않은 인가코드입니다."),
    INVALID_TOKEN(401, "U002", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(401, "U003", "만료된 토큰입니다."),
    DIFFERENT_CATEGORY(401, "U004", "카테고리가 다른 토큰입니다.");


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
