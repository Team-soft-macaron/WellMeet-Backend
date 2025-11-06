-- Owner 테이블
CREATE TABLE owner
(
    id                  VARCHAR(255) PRIMARY KEY,
    name                VARCHAR(255) NOT NULL,
    email               VARCHAR(255) NOT NULL,
    reservation_enabled BOOLEAN      NOT NULL DEFAULT TRUE,
    review_enabled      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at          DATETIME(6) NOT NULL,
    updated_at          DATETIME(6) NOT NULL,
    UNIQUE KEY unique_email (email)
);