package com.wellmeet.domain.member.exception;

import lombok.Getter;

@Getter
public enum MemberErrorCode {

    MEMBER_NOT_FOUND(404, "해당 유저를 찾을 수 없습니다."),
    MEMBER_NAME_INVALID(400, "유효하지 않은 유저 이름입니다."),
    MEMBER_NICKNAME_INVALID(400, "유효하지 않은 유저 닉네임입니다."),
    MEMBER_RESTAURANT_NOT_FOUND(404, "즐겨찾기 하지 않은 레스토랑입니다."),
    ;

    private final int statusCode;
    private final String message;

    MemberErrorCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
