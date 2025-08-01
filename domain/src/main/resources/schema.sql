CREATE TABLE IF NOT EXISTS member
(
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                 VARCHAR(255) NOT NULL,
    nickname             VARCHAR(100) NOT NULL,
    email                VARCHAR(255) NOT NULL,
    reservation_enabled  BOOLEAN DEFAULT TRUE,
    remind_enabled       BOOLEAN DEFAULT TRUE,
    review_enabled       BOOLEAN DEFAULT TRUE,
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_member_nickname (nickname),
    INDEX idx_member_email (email)
);

CREATE TABLE IF NOT EXISTS restaurant
(
    id         VARCHAR(255) PRIMARY KEY,
    name       VARCHAR(200) NOT NULL,
    address    VARCHAR(500) NOT NULL,
    thumbnail  VARCHAR(500),
    latitude   DOUBLE NOT NULL,
    longitude  DOUBLE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_restaurant_location (latitude, longitude),
    INDEX idx_restaurant_name (name)
);

CREATE TABLE IF NOT EXISTS owner
(
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                 VARCHAR(255) NOT NULL,
    email                VARCHAR(255) NOT NULL,
    password             VARCHAR(255),
    reservation_enabled  BOOLEAN DEFAULT TRUE,
    review_enabled       BOOLEAN DEFAULT TRUE,
    restaurant_id        VARCHAR(255),
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurant (id),
    INDEX idx_owner_email (email),
    INDEX idx_owner_restaurant (restaurant_id)
);

CREATE TABLE IF NOT EXISTS favorite_restaurant
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id     BIGINT NOT NULL,
    restaurant_id VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurant (id),
    UNIQUE KEY unique_member_restaurant (member_id, restaurant_id),
    INDEX idx_favorite_restaurant_member (member_id),
    INDEX idx_favorite_restaurant_restaurant (restaurant_id)
);

CREATE TABLE IF NOT EXISTS available_date
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    available_date DATE NOT NULL,
    available_time TIME NOT NULL,
    max_capacity   INT NOT NULL,
    is_available   BOOLEAN DEFAULT TRUE,
    restaurant_id  VARCHAR(255),
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurant (id),
    INDEX idx_available_date_restaurant (restaurant_id),
    INDEX idx_available_date_datetime (available_date, available_time)
);

CREATE TABLE IF NOT EXISTS business_hour
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    day_of_week       VARCHAR(20) NOT NULL,
    open_time         TIME NOT NULL,
    close_time        TIME NOT NULL,
    break_start_time  TIME,
    break_end_time    TIME,
    restaurant_id     VARCHAR(255),
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurant (id),
    INDEX idx_business_hour_restaurant (restaurant_id),
    INDEX idx_business_hour_day (day_of_week)
);

CREATE TABLE IF NOT EXISTS review
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    content       TEXT NOT NULL,
    rating        DOUBLE NOT NULL,
    situation     VARCHAR(50) NOT NULL,
    restaurant_id VARCHAR(255) NOT NULL,
    member_id     BIGINT NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurant (id),
    FOREIGN KEY (member_id) REFERENCES member (id),
    CHECK (rating >= 0.0 AND rating <= 5.0),
    CHECK (situation IN ('DATE', 'FAMILY', 'BUSINESS')),
    INDEX idx_review_restaurant (restaurant_id),
    INDEX idx_review_member (member_id)
);

CREATE TABLE IF NOT EXISTS menu
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(200) NOT NULL,
    description   TEXT,
    price         INT NOT NULL,
    restaurant_id VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurant (id),
    INDEX idx_menu_restaurant (restaurant_id),
    INDEX idx_menu_price (price)
);

CREATE TABLE IF NOT EXISTS tag
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_tag_name (name)
);

CREATE TABLE IF NOT EXISTS review_tag
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    review_id  BIGINT NOT NULL,
    tag_id     BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (review_id) REFERENCES review (id),
    FOREIGN KEY (tag_id) REFERENCES tag (id),
    UNIQUE KEY unique_review_tag (review_id, tag_id)
);

CREATE TABLE IF NOT EXISTS premium_option
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(200) NOT NULL,
    description   TEXT,
    price         INT NOT NULL,
    restaurant_id VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurant (id),
    INDEX idx_premium_option_restaurant (restaurant_id)
);

CREATE TABLE IF NOT EXISTS reservation
(
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_date_time  TIMESTAMP NOT NULL,
    status                 VARCHAR(50) NOT NULL,
    purpose                VARCHAR(500) NOT NULL,
    restaurant_id          VARCHAR(255) NOT NULL,
    member_id              BIGINT NOT NULL,
    party_size             INT NOT NULL,
    special_request        TEXT,
    created_at             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurant (id),
    FOREIGN KEY (member_id) REFERENCES member (id),
    CHECK (status IN ('PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELED')),
    INDEX idx_reservation_restaurant (restaurant_id),
    INDEX idx_reservation_member (member_id),
    INDEX idx_reservation_datetime (reservation_date_time)
);

CREATE TABLE IF NOT EXISTS selected_premium_option
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_id    BIGINT NOT NULL,
    premium_option_id BIGINT NOT NULL,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservation (id),
    FOREIGN KEY (premium_option_id) REFERENCES premium_option (id),
    UNIQUE KEY unique_reservation_premium_option (reservation_id, premium_option_id)
);