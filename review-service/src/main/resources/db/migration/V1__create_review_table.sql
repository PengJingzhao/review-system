-- V1__create_review_table.sql
CREATE TABLE review
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    content    VARCHAR(50),
    user_id    INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);