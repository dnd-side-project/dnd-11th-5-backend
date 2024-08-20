CREATE TABLE `festival_user_type`
(
    `festival_user_type_id` bigint   NOT NULL,
    `festival_id`           bigint   NOT NULL,
    `user_type_id`          bigint   NOT NULL,
    `created_at`            datetime NOT NULL,
    `updated_at`            datetime NULL,
    PRIMARY KEY (`festival_user_type_id`)
);
