CREATE TABLE IF NOT EXISTS member
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    nickname   VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS restaurant
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(200) NOT NULL,
    address    VARCHAR(500) NOT NULL,
    thumbnail  VARCHAR(500),
    latitude   DOUBLE NOT NULL,
    longitude  DOUBLE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS vibe
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS crawling_review
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    content       TEXT,
    restaurant_id BIGINT NOT NULL,
    created_at    TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurant (id)
);

CREATE TABLE IF NOT EXISTS review
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    content       TEXT,
    rating        DOUBLE NOT NULL,
    situation     VARCHAR(50),
    restaurant_id BIGINT NOT NULL,
    member_id     BIGINT NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurant (id),
    FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE IF NOT EXISTS crawling_review_vibe
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    crawling_review_id BIGINT NOT NULL,
    vibe_id            BIGINT NOT NULL,
    FOREIGN KEY (crawling_review_id) REFERENCES crawling_review (id),
    FOREIGN KEY (vibe_id) REFERENCES vibe (id),
    UNIQUE KEY unique_crawling_review_vibe (crawling_review_id, vibe_id)
);

CREATE TABLE IF NOT EXISTS menu
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(200) NOT NULL,
    description   TEXT,
    price         INT NOT NULL,
    restaurant_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurant (id)
);

CREATE TABLE IF NOT EXISTS tag
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS review_tag
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    review_id BIGINT NOT NULL,
    tag_id    BIGINT NOT NULL,
    FOREIGN KEY (review_id) REFERENCES review (id),
    FOREIGN KEY (tag_id) REFERENCES tag (id),
    UNIQUE KEY unique_review_tag (review_id, tag_id)
);
