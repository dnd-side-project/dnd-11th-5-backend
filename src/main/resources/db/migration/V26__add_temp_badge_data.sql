ALTER TABLE `badge`
    ADD COLUMN `description` varchar(255) NOT NULL;
ALTER TABLE `badge`
    ADD COLUMN `image_url` varchar(2048) NOT NULL;
ALTER TABLE `badge`
    ADD COLUMN `type` varchar(255) NOT NULL;
ALTER TABLE `badge`
    ADD INDEX `idx_badge_type` (`type`);

INSERT INTO `badge` (`name`, `description`, `image_url`, `type`)
VALUES ('회원 가입', '피에스타 회원가입!', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge_temp.png', 'USER'),
       ('첫 리뷰 작성', '피에스타 첫 리뷰', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge_temp.png', 'REVIEW'),
       ('첫 등록', '피에스타 첫 등록', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge_temp.png', 'FESTIVAL'),
       ('열혈 참여러', '✨리뷰 5개 작성✨', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge_temp.png', 'REVIEW'),
       ('역사 탐방러', '리뷰 2개째!', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge_temp.png', 'REVIEW'),
       ('음악 탐방러', '리뷰 2개째!', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge_temp.png', 'REVIEW'),
       ('액티비티 탐방러', '리뷰 2개째!', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge_temp.png', 'REVIEW'),
       ('음식&술 탐방러', '리뷰 2개째!', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge_temp.png', 'REVIEW'),
       ('불꽃축제 탐방러', '리뷰 2개째!', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge_temp.png', 'REVIEW'),
       ('자연 탐방러', '리뷰 2개째!', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge_temp.png', 'REVIEW'),
       ('야간 탐방러', '리뷰 2개째!', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge_temp.png', 'REVIEW'),
       ('미술 탐방러', '리뷰 2개째!', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge_temp.png', 'REVIEW'),
       ('문화 탐방러', '리뷰 2개째!', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge_temp.png', 'REVIEW'),
       ('이색축제 탐방러', '리뷰 2개째!', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge_temp.png', 'REVIEW');
