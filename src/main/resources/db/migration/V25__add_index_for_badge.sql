ALTER TABLE `festival`
    ADD INDEX idx_festival_user_id (`user_id`);

ALTER TABLE `review`
    ADD INDEX idx_review_user_id (`user_id`);

ALTER TABLE `user_badge`
    ADD INDEX idx_user_badge_user_id (`user_id`);

ALTER TABLE `user_badge`
    ADD INDEX idx_user_badge_badge_id (`badge_id`);
