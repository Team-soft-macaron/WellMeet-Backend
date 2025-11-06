-- Member 테이블
CREATE TABLE member
(
    id                  VARCHAR(255) PRIMARY KEY,
    name                VARCHAR(255) NOT NULL,
    nickname            VARCHAR(255) NOT NULL,
    email               VARCHAR(255) NOT NULL,
    phone               VARCHAR(255) NOT NULL,
    reservation_enabled BOOLEAN      NOT NULL DEFAULT TRUE,
    remind_enabled      BOOLEAN      NOT NULL DEFAULT TRUE,
    review_enabled      BOOLEAN      NOT NULL DEFAULT TRUE,
    is_vip              BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at          DATETIME(6) NOT NULL,
    updated_at          DATETIME(6) NOT NULL,
    UNIQUE KEY unique_email (email),
    UNIQUE KEY unique_phone (phone)
);

-- FavoriteRestaurant 테이블
-- Microservices 아키텍처: 물리적 외래키 제약 제거, 논리적 참조만 유지
CREATE TABLE favorite_restaurant
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id     VARCHAR(255) NOT NULL,        -- 논리적 참조: member
    restaurant_id VARCHAR(255) NOT NULL,        -- 논리적 참조: domain-restaurant
    created_at    DATETIME(6) NOT NULL,
    updated_at    DATETIME(6) NOT NULL,
    UNIQUE KEY unique_member_restaurant (member_id, restaurant_id),
    INDEX idx_favorite_restaurant_member (member_id),
    INDEX idx_favorite_restaurant_restaurant (restaurant_id)
);