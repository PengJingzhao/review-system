-- V1__create_user_table.sql
CREATE TABLE user
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(50),
    email      VARCHAR(100),
    phone      VARCHAR(50),
    password   VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);