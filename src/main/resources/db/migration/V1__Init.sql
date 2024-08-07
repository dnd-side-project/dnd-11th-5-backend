-- 삭제할 테이블 목록 삭제
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `category`;
DROP TABLE IF EXISTS `mood`;
DROP TABLE IF EXISTS `log`;
DROP TABLE IF EXISTS `festival_bookmark`;
DROP TABLE IF EXISTS `companion`;
DROP TABLE IF EXISTS `priority`;
DROP TABLE IF EXISTS `festival`;
DROP TABLE IF EXISTS `user_category`;
DROP TABLE IF EXISTS `user_mood`;
DROP TABLE IF EXISTS `user_companion`;
DROP TABLE IF EXISTS `Sido`;
DROP TABLE IF EXISTS `user_priority`;
DROP TABLE IF EXISTS `keyword`;
DROP TABLE IF EXISTS `log_keyword`;
DROP TABLE IF EXISTS `log_image`;
DROP TABLE IF EXISTS `review`;
DROP TABLE IF EXISTS `review_keyword`;
DROP TABLE IF EXISTS `review_image`;
DROP TABLE IF EXISTS `review_like`;
DROP TABLE IF EXISTS `festival_category`;
DROP TABLE IF EXISTS `festival_mood`;
DROP TABLE IF EXISTS `oauth_user`;
DROP TABLE IF EXISTS `role`;
DROP TABLE IF EXISTS `user_role`;
DROP TABLE IF EXISTS `local_user`;
DROP TABLE IF EXISTS `festival_image`;
DROP TABLE IF EXISTS `activity`;
DROP TABLE IF EXISTS `badge`;
DROP TABLE IF EXISTS `user_badge`;
DROP TABLE IF EXISTS `user_type`;

-- 테이블 생성
CREATE TABLE `user`
(
    `user_id`        bigint AUTO_INCREMENT NOT NULL,
    `user_type_id`   bigint       NOT NULL,
    `nickname`       varchar(255) NOT NULL,
    `status_message` varchar(255) NULL,
    `profile_image`  varchar(255) NULL,
    `created_at`     datetime     NOT NULL,
    `updated_at`     datetime NULL,
    PRIMARY KEY (`user_id`)
);

CREATE TABLE `user_type`
(
    `user_type_id`  bigint AUTO_INCREMENT NOT NULL,
    `name`          varchar(255) NOT NULL,
    `profile_image` varchar(255) NOT NULL,
    PRIMARY KEY (`user_type_id`)
);

CREATE TABLE `category`
(
    `category_id` bigint AUTO_INCREMENT NOT NULL,
    `category`    varchar(255) NOT NULL,
    PRIMARY KEY (`category_id`)
);

CREATE TABLE `mood`
(
    `mood_id` bigint AUTO_INCREMENT NOT NULL,
    `mood`    varchar(255) NOT NULL,
    PRIMARY KEY (`mood_id`)
);

CREATE TABLE `log`
(
    `log_id`     bigint AUTO_INCREMENT NOT NULL,
    `user_id`    bigint       NOT NULL,
    `content`    varchar(255) NOT NULL,
    `date`       datetime     NOT NULL,
    `is_public`  bit(1)       NOT NULL DEFAULT 0,
    `created_at` datetime     NOT NULL,
    `updated_at` datetime NULL,
    PRIMARY KEY (`log_id`)
);

CREATE TABLE `festival_bookmark`
(
    `festival_bookmark_id` bigint AUTO_INCREMENT NOT NULL,
    `user_id`              bigint   NOT NULL,
    `festival_id`          bigint   NOT NULL,
    `created_at`           datetime NOT NULL,
    `updated_at`           datetime NULL,
    PRIMARY KEY (`festival_bookmark_id`)
);

CREATE TABLE `companion`
(
    `companion_type_id` bigint AUTO_INCREMENT NOT NULL,
    `companion_type`    varchar(255) NOT NULL,
    PRIMARY KEY (`companion_type_id`)
);

CREATE TABLE `priority`
(
    `priority_id` bigint AUTO_INCREMENT NOT NULL,
    `priority`    varchar(255) NOT NULL,
    PRIMARY KEY (`priority_id`)
);

CREATE TABLE `festival`
(
    `festival_id`   bigint AUTO_INCREMENT NOT NULL,
    `name`          varchar(255)    NOT NULL,
    `start_date`    datetime        NOT NULL,
    `end_date`      datetime        NOT NULL,
    `address`       varchar(255)    NOT NULL,
    `sido_id`       bigint          NOT NULL,
    `sigungu`       varchar(255)    NOT NULL,
    `latitude`      decimal(13, 10) NOT NULL,
    `longitude`     decimal(13, 10) NOT NULL,
    `tip`           varchar(255)    NOT NULL,
    `homepage_url`  varchar(255) NULL,
    `instagram_url` varchar(255) NULL,
    `fee`           bigint NULL,
    `description`   varchar(255)    NOT NULL,
    `ticket_link`   varchar(255) NULL,
    `playtime`      datetime NULL,
    `is_pending`    bit(1)          NOT NULL,
    `created_at`    datetime        NOT NULL,
    `updated_at`    datetime NULL,
    PRIMARY KEY (`festival_id`)
);

CREATE TABLE `user_category`
(
    `user_category_id` bigint AUTO_INCREMENT NOT NULL,
    `user_id`          bigint   NOT NULL,
    `category_id`      bigint   NOT NULL,
    `created_at`       datetime NOT NULL,
    `updated_at`       datetime NULL,
    PRIMARY KEY (`user_category_id`)
);

CREATE TABLE `user_mood`
(
    `user_mood_id` bigint AUTO_INCREMENT NOT NULL,
    `user_id`      bigint   NOT NULL,
    `mood_id`      bigint   NOT NULL,
    `created_at`   datetime NOT NULL,
    `updated_at`   datetime NULL,
    PRIMARY KEY (`user_mood_id`)
);

CREATE TABLE `user_companion`
(
    `user_companion_id` bigint AUTO_INCREMENT NOT NULL,
    `user_id`           bigint   NOT NULL,
    `companion_type_id` bigint   NOT NULL,
    `created_at`        datetime NOT NULL,
    `updated_at`        datetime NULL,
    PRIMARY KEY (`user_companion_id`)
);

CREATE TABLE `Sido`
(
    `sido_id` bigint AUTO_INCREMENT NOT NULL,
    `sido`    varchar(255) NOT NULL,
    `code`    bigint       NOT NULL,
    PRIMARY KEY (`sido_id`)
);

CREATE TABLE `user_priority`
(
    `user_priority_id` bigint AUTO_INCREMENT NOT NULL,
    `user_id`          bigint NOT NULL,
    `priority_id`      bigint NOT NULL,
    `created_at`       datetime NULL,
    `updated_at`       datetime NULL,
    PRIMARY KEY (`user_priority_id`)
);

CREATE TABLE `keyword`
(
    `keyword_id` bigint AUTO_INCREMENT NOT NULL,
    `keyword`    varchar(255) NOT NULL,
    PRIMARY KEY (`keyword_id`)
);

CREATE TABLE `log_keyword`
(
    `log_keyword_id` bigint AUTO_INCREMENT NOT NULL,
    `log_id`         bigint NOT NULL,
    `keyword_id`     bigint NOT NULL,
    PRIMARY KEY (`log_keyword_id`)
);

CREATE TABLE `log_image`
(
    `log_image_id` bigint AUTO_INCREMENT NOT NULL,
    `log_id`       bigint       NOT NULL,
    `image_url`    varchar(255) NOT NULL,
    `created_at`   datetime     NOT NULL,
    `updated_at`   datetime NULL,
    PRIMARY KEY (`log_image_id`)
);

CREATE TABLE `review`
(
    `review_id`   bigint AUTO_INCREMENT NOT NULL,
    `user_id`     bigint       NOT NULL,
    `festival_id` bigint       NOT NULL,
    `score`       tinyint      NOT NULL,
    `content`     varchar(255) NOT NULL,
    `created_at`  datetime     NOT NULL,
    `updated_at`  datetime NULL,
    PRIMARY KEY (`review_id`)
);

CREATE TABLE `review_keyword`
(
    `review_keyword_id` bigint AUTO_INCREMENT NOT NULL,
    `review_id`         bigint   NOT NULL,
    `keyword_id`        bigint   NOT NULL,
    `created_at`        datetime NOT NULL,
    `updated_at`        datetime NULL,
    PRIMARY KEY (`review_keyword_id`)
);

CREATE TABLE `review_image`
(
    `review_image_id` bigint AUTO_INCREMENT NOT NULL,
    `review_id`       bigint       NOT NULL,
    `image_url`       varchar(255) NOT NULL,
    `created_at`      datetime     NOT NULL,
    `updated_at`      datetime NULL,
    PRIMARY KEY (`review_image_id`)
);

CREATE TABLE `review_like`
(
    `review_like_id` bigint AUTO_INCREMENT NOT NULL,
    `user_id`        bigint   NOT NULL,
    `review_id`      bigint   NOT NULL,
    `created_at`     datetime NOT NULL,
    `updated_at`     datetime NULL,
    PRIMARY KEY (`review_like_id`)
);

CREATE TABLE `festival_category`
(
    `festival_category_id` bigint AUTO_INCREMENT NOT NULL,
    `festival_id`          bigint   NOT NULL,
    `category_id`          bigint   NOT NULL,
    `created_at`           datetime NOT NULL,
    `updated_at`           datetime NULL,
    PRIMARY KEY (`festival_category_id`)
);

CREATE TABLE `festival_mood`
(
    `festival_mood_id` bigint AUTO_INCREMENT NOT NULL,
    `mood_id`          bigint   NOT NULL,
    `festival_id`      bigint   NOT NULL,
    `created_at`       datetime NOT NULL,
    `updated_at`       datetime NULL,
    PRIMARY KEY (`festival_mood_id`)
);

CREATE TABLE `oauth_user`
(
    `user_id`     bigint       NOT NULL,
    `provider_id` bigint       NOT NULL,
    `provider`    varchar(255) NOT NULL DEFAULT 'Kakao',
    PRIMARY KEY (`user_id`)
);

CREATE TABLE `role`
(
    `role_id`    bigint AUTO_INCREMENT NOT NULL,
    `authority`  varchar(255) NOT NULL DEFAULT 'User',
    `created_at` datetime     NOT NULL,
    `updated_at` datetime NULL,
    PRIMARY KEY (`role_id`)
);

CREATE TABLE `user_role`
(
    `role_id`    bigint   NOT NULL,
    `user_id`    bigint   NOT NULL,
    `created_at` datetime NOT NULL,
    `updated_at` datetime NULL,
    PRIMARY KEY (`role_id`, `user_id`)
);

CREATE TABLE `local_user`
(
    `user_id`  bigint       NOT NULL,
    `password` varchar(255) NOT NULL,
    PRIMARY KEY (`user_id`)
);

CREATE TABLE `festival_image`
(
    `festival_image_id` bigint AUTO_INCREMENT NOT NULL,
    `festival_id`       bigint       NOT NULL,
    `image_url`         varchar(255) NOT NULL,
    `created_at`        datetime     NOT NULL,
    `updated_at`        datetime NULL,
    PRIMARY KEY (`festival_image_id`)
);

CREATE TABLE `activity`
(
    `activity_id`               bigint AUTO_INCREMENT NOT NULL,
    `first_review_created_at`   datetime NULL,
    `first_log_created_at`      datetime NULL,
    `review_count`              bigint NOT NULL DEFAULT 0,
    `first_festival_created_at` datetime NULL,
    `user_id`                   bigint NOT NULL,
    PRIMARY KEY (`activity_id`)
);

CREATE TABLE `badge`
(
    `badge_id` bigint AUTO_INCREMENT NOT NULL,
    `name`     varchar(255) NOT NULL,
    PRIMARY KEY (`badge_id`)
);

CREATE TABLE `user_badge`
(
    `user_badge_id` bigint AUTO_INCREMENT NOT NULL,
    `badge_id`      bigint   NOT NULL,
    `user_id`       bigint   NOT NULL,
    `created_at`    datetime NOT NULL,
    `updated_at`    datetime NULL,
    PRIMARY KEY (`user_badge_id`)
);
