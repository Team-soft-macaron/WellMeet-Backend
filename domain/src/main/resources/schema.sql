-- Owner 테이블
CREATE TABLE IF NOT EXISTS owner
(
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                 VARCHAR(255) NOT NULL,
    email                VARCHAR(255) NOT NULL,
    reservation_enabled  BOOLEAN NOT NULL DEFAULT TRUE,
    review_enabled       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at           DATETIME(6) NOT NULL,
    updated_at           DATETIME(6) NOT NULL,
    INDEX idx_owner_email (email)
);

-- Member 테이블
CREATE TABLE IF NOT EXISTS member
(
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                 VARCHAR(255) NOT NULL,
    nickname             VARCHAR(255) NOT NULL,
    email                VARCHAR(255) NOT NULL,
    phone                VARCHAR(255) NOT NULL,
    reservation_enabled  BOOLEAN NOT NULL DEFAULT TRUE,
    remind_enabled       BOOLEAN NOT NULL DEFAULT TRUE,
    review_enabled       BOOLEAN NOT NULL DEFAULT TRUE,
    is_vip               BOOLEAN NOT NULL DEFAULT FALSE,
    created_at           DATETIME(6) NOT NULL,
    updated_at           DATETIME(6) NOT NULL,
    INDEX idx_member_nickname (nickname),
    INDEX idx_member_email (email)
);

-- Restaurant 테이블
CREATE TABLE IF NOT EXISTS restaurant
(
    id         VARCHAR(255) PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    address    VARCHAR(255) NOT NULL,
    latitude   DOUBLE NOT NULL,
    longitude  DOUBLE NOT NULL,
    thumbnail  VARCHAR(255),
    owner_id   BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES owner (id),
    INDEX idx_restaurant_location (latitude, longitude),
    INDEX idx_restaurant_name (name),
    INDEX idx_restaurant_owner (owner_id)
);

-- AvailableDate 테이블
CREATE TABLE IF NOT EXISTS available_date
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    available_date  DATE NOT NULL,
    available_time  TIME NOT NULL,
    max_capacity    INT NOT NULL,
    is_available    BOOLEAN NOT NULL DEFAULT TRUE,
    restaurant_id   VARCHAR(255) NOT NULL,
    created_at      DATETIME(6) NOT NULL,
    updated_at      DATETIME(6) NOT NULL,
    FOREIGN KEY (restaurant_id) REFERENCES restaurant (id),
    INDEX idx_available_date_restaurant (restaurant_id),
    INDEX idx_available_date_datetime (available_date, available_time)
);

-- Reservation 테이블
CREATE TABLE IF NOT EXISTS reservation
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    status            VARCHAR(255) NOT NULL,
    restaurant_id     VARCHAR(255) NOT NULL,
    available_date_id BIGINT NOT NULL,
    member_id         BIGINT NOT NULL,
    party_size        INT NOT NULL,
    special_request   VARCHAR(255),
    created_at        DATETIME(6) NOT NULL,
    updated_at        DATETIME(6) NOT NULL,
    FOREIGN KEY (restaurant_id) REFERENCES restaurant (id),
    FOREIGN KEY (available_date_id) REFERENCES available_date (id),
    FOREIGN KEY (member_id) REFERENCES member (id),
    UNIQUE KEY unique_member_restaurant_available_date (member_id, restaurant_id, available_date_id),
    CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELED')),
    INDEX idx_reservation_restaurant (restaurant_id),
    INDEX idx_reservation_member (member_id),
    INDEX idx_reservation_available_date (available_date_id)
);

-- FavoriteRestaurant 테이블 (member_restaurant를 favorite_restaurant로 변경)
CREATE TABLE IF NOT EXISTS favorite_restaurant
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id     BIGINT NOT NULL,
    restaurant_id VARCHAR(255) NOT NULL,
    created_at    DATETIME(6) NOT NULL,
    updated_at    DATETIME(6) NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurant (id),
    UNIQUE KEY unique_member_restaurant (member_id, restaurant_id),
    INDEX idx_favorite_restaurant_member (member_id),
    INDEX idx_favorite_restaurant_restaurant (restaurant_id)
);

-- BusinessHour 테이블
CREATE TABLE IF NOT EXISTS business_hour
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    day_of_week       VARCHAR(255) NOT NULL,
    open_time         TIME NOT NULL,
    close_time        TIME NOT NULL,
    break_start_time  TIME,
    break_end_time    TIME,
    restaurant_id     VARCHAR(255) NOT NULL,
    created_at        DATETIME(6) NOT NULL,
    updated_at        DATETIME(6) NOT NULL,
    FOREIGN KEY (restaurant_id) REFERENCES restaurant (id),
    CHECK (day_of_week IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY')),
    INDEX idx_business_hour_restaurant (restaurant_id)
);

-- Menu 테이블
CREATE TABLE IF NOT EXISTS menu
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    description   TEXT,
    price         INT NOT NULL,
    restaurant_id VARCHAR(255) NOT NULL,
    created_at    DATETIME(6) NOT NULL,
    updated_at    DATETIME(6) NOT NULL,
    FOREIGN KEY (restaurant_id) REFERENCES restaurant (id),
    CHECK (price >= 0),
    INDEX idx_menu_restaurant (restaurant_id),
    INDEX idx_menu_price (price)
);

-- Review 테이블
CREATE TABLE IF NOT EXISTS review
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    content       VARCHAR(500) NOT NULL,
    rating        DOUBLE NOT NULL,
    situation     VARCHAR(255) NOT NULL,
    restaurant_id VARCHAR(255) NOT NULL,
    member_id     BIGINT NOT NULL,
    created_at    DATETIME(6) NOT NULL,
    updated_at    DATETIME(6) NOT NULL,
    FOREIGN KEY (restaurant_id) REFERENCES restaurant (id),
    FOREIGN KEY (member_id) REFERENCES member (id),
    CHECK (rating >= 0.0 AND rating <= 5.0),
    CHECK (situation IN ('DATE', 'FAMILY', 'BUSINESS')),
    INDEX idx_review_restaurant (restaurant_id),
    INDEX idx_review_member (member_id)
);

-- ReviewTag 테이블
CREATE TABLE IF NOT EXISTS review_tag
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    review_id  BIGINT NOT NULL,
    name       VARCHAR(255) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    FOREIGN KEY (review_id) REFERENCES review (id),
    INDEX idx_review_tag_review (review_id)
);
