package com.wellmeet.recommend.review.domain;

import lombok.Getter;

@Getter
public enum Situation {

    DATE("데이트", "http://example.com/logo/date.png"),
    FAMILY("가족 모임", "http://example.com/logo/family.png"),
    BUSINESS("비즈니스 미팅", "http://example.com/logo/business.png"),
    ;

    private final String name;
    private final String logo;

    Situation(String name, String logo) {
        this.name = name;
        this.logo = logo;
    }
}
