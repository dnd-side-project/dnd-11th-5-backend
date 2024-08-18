-- `role` 테이블에서 `created_at`과 `updated_at` 필드 삭제
ALTER TABLE `role`
    DROP COLUMN `created_at`,
    DROP COLUMN `updated_at`;

-- `role` 테이블에 데이터 추가
INSERT INTO `role` (`role_id`,`authority`)
VALUES (1, 'ROLE_USER'),
       (2, 'ROLE_ADMIN');

-- `user_role` 테이블 삭제
DROP TABLE IF EXISTS `user_role`;

-- mood에 '잔잔한'추가
DELETE FROM `mood` WHERE `mood_id` IN (16, 17);

INSERT INTO `mood` (`mood_id`, `mood`)
VALUES (16, '잔잔한'),
       (17, '재미있는'),
       (18, '감동이 있는');