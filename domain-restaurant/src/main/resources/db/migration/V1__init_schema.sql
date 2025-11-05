-- Restaurant 테이블
-- Microservices 아키텍처: 물리적 외래키 제약 제거, 논리적 참조만 유지
CREATE TABLE restaurant
(
    id            VARCHAR(255) PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    description   TEXT,
    address       VARCHAR(255) NOT NULL,
    latitude      DOUBLE       NOT NULL,
    longitude     DOUBLE       NOT NULL,
    phone_number  VARCHAR(255),
    owner_id      VARCHAR(255) NOT NULL,        -- 논리적 참조: domain-owner
    thumbnail_url VARCHAR(255),
    created_at    DATETIME(6) NOT NULL,
    updated_at    DATETIME(6) NOT NULL,
    INDEX idx_restaurant_owner (owner_id),
    INDEX idx_restaurant_location (latitude, longitude)
);

-- AvailableDate 테이블
CREATE TABLE available_date
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    date          DATE         NOT NULL,
    time          TIME         NOT NULL,
    max_capacity  INT          NOT NULL,
    available     BOOLEAN      NOT NULL DEFAULT TRUE,
    restaurant_id VARCHAR(255) NOT NULL,
    created_at    DATETIME(6) NOT NULL,
    updated_at    DATETIME(6) NOT NULL,
    CONSTRAINT fk_available_date_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurant (id) ON DELETE CASCADE,
    INDEX idx_available_date_restaurant (restaurant_id),
    INDEX idx_available_date_date_time (date, time)
);

-- BusinessHour 테이블
CREATE TABLE business_hour
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    day_of_week   VARCHAR(255) NOT NULL,
    open_time     TIME         NOT NULL,
    close_time    TIME         NOT NULL,
    restaurant_id VARCHAR(255) NOT NULL,
    created_at    DATETIME(6) NOT NULL,
    updated_at    DATETIME(6) NOT NULL,
    CONSTRAINT fk_business_hour_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurant (id) ON DELETE CASCADE,
    CHECK (day_of_week IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY')),
    INDEX idx_business_hour_restaurant (restaurant_id)
);

-- Menu 테이블
CREATE TABLE menu
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    description   TEXT,
    price         INT          NOT NULL,
    restaurant_id VARCHAR(255) NOT NULL,
    created_at    DATETIME(6) NOT NULL,
    updated_at    DATETIME(6) NOT NULL,
    CONSTRAINT fk_menu_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurant (id) ON DELETE CASCADE,
    INDEX idx_menu_restaurant (restaurant_id)
);

-- Review 테이블
-- Microservices 아키텍처: 물리적 외래키 제약 제거, 논리적 참조만 유지
CREATE TABLE review
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    content       TEXT         NOT NULL,
    rating        DOUBLE       NOT NULL,
    tag           VARCHAR(255) NOT NULL,
    restaurant_id VARCHAR(255) NOT NULL,
    member_id     VARCHAR(255) NOT NULL,        -- 논리적 참조: domain-member
    created_at    DATETIME(6) NOT NULL,
    updated_at    DATETIME(6) NOT NULL,
    CONSTRAINT fk_review_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurant (id) ON DELETE CASCADE,
    CHECK (tag IN ('DATE', 'BUSINESS', 'FAMILY', 'FRIEND', 'SOLO')),
    CHECK (rating >= 0.0 AND rating <= 5.0),
    INDEX idx_review_restaurant (restaurant_id),
    INDEX idx_review_member (member_id)
);