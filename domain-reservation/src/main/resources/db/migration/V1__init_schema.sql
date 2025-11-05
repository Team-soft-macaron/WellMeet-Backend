-- Reservation 테이블
-- Microservices 아키텍처: 물리적 외래키 제약 제거, 논리적 참조만 유지
CREATE TABLE reservation
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    status            VARCHAR(255) NOT NULL,
    restaurant_id     VARCHAR(255) NOT NULL,        -- 논리적 참조: domain-restaurant
    available_date_id BIGINT NOT NULL,              -- 논리적 참조: domain-restaurant
    member_id         VARCHAR(255) NOT NULL,        -- 논리적 참조: domain-member
    party_size        INT NOT NULL,
    special_request   VARCHAR(255),
    created_at        DATETIME(6) NOT NULL,
    updated_at        DATETIME(6) NOT NULL,
    UNIQUE KEY unique_member_restaurant_available_date (member_id, restaurant_id, available_date_id),
    CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELED')),
    INDEX idx_reservation_restaurant (restaurant_id),
    INDEX idx_reservation_member (member_id),
    INDEX idx_reservation_available_date (available_date_id)
);