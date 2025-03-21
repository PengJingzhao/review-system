-- V2__create_voucher_table.sql
CREATE TABLE `shops`
(
    `id`                 INT AUTO_INCREMENT PRIMARY KEY,
    `shop_name`          VARCHAR(255) NOT NULL,
    `shop_logo`          VARCHAR(255),
    `description`        TEXT,
    `category`           VARCHAR(255),
    `address`            VARCHAR(255),
    `phone`              VARCHAR(20),
    `email`              VARCHAR(255),
    `website_url`        VARCHAR(255),
    `social_media_links` JSON,
    `status`             TINYINT      DEFAULT 0,
    `created_at`         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at`         TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);