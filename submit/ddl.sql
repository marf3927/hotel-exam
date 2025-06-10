CREATE TABLE `hotels`
(
    `hotel_id`   BIGINT       NOT NULL,
    `name`       VARCHAR(100) NOT NULL,
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`hotel_id`)
);

CREATE TABLE `rooms`
(
    `room_id`           BIGINT         NOT NULL AUTO_INCREMENT,
    `hotel_id`          BIGINT         NOT NULL,
    `name`              VARCHAR(100)   NOT NULL,
    `capacity`          TINYINT        NOT NULL,
    `floor`             TINYINT        NOT NULL,
    `bathtub_flag`      BOOLEAN        NOT NULL DEFAULT FALSE,
    `view_type`         TINYINT        NOT NULL,
    `price`             DECIMAL(10, 2) NOT NULL,
    `peak_season_price` DECIMAL(10, 2) NOT NULL,
    `created_at`        DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`room_id`),
    INDEX `idx_rooms_hotel_id` (`hotel_id`),
    CONSTRAINT `fk_rooms_hotel_id` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`hotel_id`)
);

CREATE TABLE `reservations`
(
    `reservation_id` BIGINT                                       NOT NULL AUTO_INCREMENT,
    `user_id`        BIGINT                                       NOT NULL,
    `room_id`        BIGINT                                       NOT NULL,
    `check_in_date`  DATE                                         NOT NULL,
    `check_out_date` DATE                                         NOT NULL,
    `pax`            INT                                          NOT NULL,
    `total_price`    DECIMAL(12, 2)                               NOT NULL,
    `status`         ENUM ('CONFIRMED', 'CANCELED', 'CHECKED_IN') NOT NULL DEFAULT 'CONFIRMED',
    `created_at`     DATETIME                                     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     DATETIME                                     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`reservation_id`),
    INDEX `idx_reservations_user_id` (`user_id`),
    INDEX `idx_reservations_room_id` (`room_id`),
    CONSTRAINT `fk_reservations_room_id` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`)
);

CREATE TABLE `daily_room_products`
(
    `product_id`     BIGINT   NOT NULL AUTO_INCREMENT,
    `room_id`        BIGINT   NOT NULL,
    `stay_date`      DATE     NOT NULL,
    `is_peak_season` BOOLEAN  NOT NULL,
    `reservation_id` BIGINT   NULL,
    `created_at`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`product_id`),
    UNIQUE KEY `uk_daily_room_date` (`room_id`, `stay_date`),
    INDEX `idx_drp_reservation_id` (`reservation_id`),
    CONSTRAINT `fk_drp_room_id` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`),
    CONSTRAINT `fk_drp_reservation_id` FOREIGN KEY (`reservation_id`) REFERENCES `reservations` (`reservation_id`) ON DELETE SET NULL
);

CREATE TABLE `images`
(
    `image_id`   BIGINT       NOT NULL AUTO_INCREMENT,
    `url`        VARCHAR(255) NOT NULL,
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`image_id`)
);

CREATE TABLE `reviews`
(
    `review_id`      BIGINT       NOT NULL AUTO_INCREMENT,
    `reservation_id` BIGINT       NOT NULL,
    `user_id`        BIGINT       NOT NULL,
    `rating`         TINYINT      NOT NULL DEFAULT 5,
    `title`          VARCHAR(100) NOT NULL,
    `content`        TEXT         NOT NULL,
    `is_visible`     BOOLEAN      NOT NULL DEFAULT TRUE,
    `created_at`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`review_id`),
    INDEX `idx_reviews_user_id` (`user_id`),
    INDEX `idx_reviews_reservation_id` (`reservation_id`),
    CONSTRAINT `fk_reviews_reservation_id` FOREIGN KEY (`reservation_id`) REFERENCES `reservations` (`reservation_id`)
);

CREATE TABLE `review_images`
(
    `review_image_id` BIGINT NOT NULL AUTO_INCREMENT,
    `review_id`       BIGINT NOT NULL,
    `image_id`        BIGINT NOT NULL,
    `priority`        INT    NOT NULL DEFAULT 0,
    PRIMARY KEY (`review_image_id`),
    INDEX `idx_ri_review_id` (`review_id`),
    INDEX `idx_ri_image_id` (`image_id`),
    CONSTRAINT `fk_ri_review_id` FOREIGN KEY (`review_id`) REFERENCES `reviews` (`review_id`),
    CONSTRAINT `fk_ri_image_id` FOREIGN KEY (`image_id`) REFERENCES `images` (`image_id`)
);

CREATE TABLE `room_images`
(
    `room_image_id` BIGINT NOT NULL AUTO_INCREMENT,
    `room_id`       BIGINT NOT NULL,
    `image_id`      BIGINT NOT NULL,
    `priority`      INT    NOT NULL DEFAULT 0,
    PRIMARY KEY (`room_image_id`),
    INDEX `idx_rmi_room_id` (`room_id`),
    INDEX `idx_rmi_image_id` (`image_id`),
    CONSTRAINT `fk_rmi_room_id` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`),
    CONSTRAINT `fk_rmi_image_id` FOREIGN KEY (`image_id`) REFERENCES `images` (`image_id`)
);

CREATE TABLE `point_policies`
(
    `point_policy_id` BIGINT         NOT NULL AUTO_INCREMENT,
    `policy_name`     VARCHAR(100)   NOT NULL,
    `action_code`     VARCHAR(50)    NOT NULL UNIQUE,
    `point_value`     DECIMAL(10, 2) NOT NULL,
    `is_active`       BOOLEAN        NOT NULL DEFAULT TRUE,
    `expiry_days`     INT            NULL,
    `created_at`      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`point_policy_id`)
);

CREATE TABLE `point_logs`
(
    `point_log_id`    BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`         BIGINT       NOT NULL,
    `point_policy_id` BIGINT       NULL,
    `action_code`     VARCHAR(50)  NOT NULL,
    `points_change`   INT          NOT NULL,
    `related_id`      BIGINT       NULL,
    `description`     VARCHAR(255) NULL,
    `expiry_date`     DATE         NULL,
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`point_log_id`),
    INDEX `idx_pl_user_id` (`user_id`),
    INDEX `idx_pl_policy_id` (`point_policy_id`),
    CONSTRAINT `fk_pl_policy_id` FOREIGN KEY (`point_policy_id`) REFERENCES `point_policies` (`point_policy_id`)
);