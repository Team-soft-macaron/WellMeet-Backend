package com.wellmeet.domain.restaurant.review.domain;

import lombok.Getter;

@Getter
public enum Situation {

    DATE("데이트", "💕"),
    FAMILY("가족 모임", "🎎"),
    BUSINESS("비즈니스 미팅", "🧳"),
    ;

    private final String name;
    private final String logo;

    Situation(String name, String logo) {
        this.name = name;
        this.logo = logo;
    }
}
