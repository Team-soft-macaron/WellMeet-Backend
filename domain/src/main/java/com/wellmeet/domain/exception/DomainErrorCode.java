package com.wellmeet.domain.exception;

import lombok.Getter;

@Getter
public enum DomainErrorCode {

    INVALID_LATITUDE(400, "유효하지 않은 위도입니다."),
    INVALID_LONGITUDE(400, "유효하지 않은 경도입니다."),
    INVALID_RATING(400, "유효하지 않은 평점입니다."),
    RESTAURANT_NOT_FOUND(404, "해당 레스토랑을 찾을 수 없습니다."),
    PREMIUM_OPTION_NOT_FOUND(404, "해당 프리미엄 옵션을 찾을 수 없습니다."),
    UNAUTHORIZED_RESERVATION_ACCESS(400, "예약에 대한 권한이 없습니다."),
    MEMBER_NOT_FOUND(404, "해당 유저를 찾을 수 없습니다."),
    MEMBER_RESTAURANT_NOT_FOUND(404, "즐겨찾기 하지 않은 레스토랑입니다."),
    OWNER_NOT_FOUND(404, "해당 사장님을 찾을 수 없습니다."),
    RESERVATION_NOT_FOUND(404, "해당 예약을 찾을 수 없습니다."),
    ;

    private final int statusCode;
    private final String message;

    DomainErrorCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
