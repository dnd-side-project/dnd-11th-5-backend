CREATE TABLE `user_role`
(
    `role_id`    bigint   NOT NULL,
    `user_id`    bigint   NOT NULL,
    `created_at` datetime NOT NULL,
    `updated_at` datetime NULL,
    PRIMARY KEY (`role_id`, `user_id`)
);