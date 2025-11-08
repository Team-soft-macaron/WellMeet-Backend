package com.wellmeet.domain.exception;

import lombok.Getter;

@Getter
public enum RestaurantErrorCode {

    INVALID_LATITUDE(400, "유효하지 않은 위도입니다."),
    INVALID_LONGITUDE(400, "유효하지 않은 경도입니다."),

    INVALID_RATING(400, "유효하지 않은 평점입니다."),
    INVALID_REVIEW_CONTENT(400, "유효하지 않은 리뷰 내용입니다."),
    INVALID_REVIEW_TAG_NAME(400, "유효하지 않은 리뷰 태그 이름입니다."),

    RESTAURANT_NOT_FOUND(404, "해당 레스토랑을 찾을 수 없습니다."),

    AVAILABLE_DATE_NOT_FOUND(404, "해당 예약 가능한 날짜를 찾을 수 없습니다."),
    NOT_ENOUGH_CAPACITY(400, "해당 시간에 예약이 불가능합니다."),
    TIME_SEQUENCE_INVALID(400, "시간 순서가 잘못되었습니다."),
    INVALID_MENU_PRICE(400, "유효하지 않은 메뉴 가격입니다."),
    ;

    private final int statusCode;
    private final String message;

    RestaurantErrorCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
