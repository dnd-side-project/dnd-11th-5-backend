DELETE
FROM `badge`;

ALTER TABLE `badge`
    AUTO_INCREMENT = 1;

INSERT INTO `badge` (`name`, `description`, `image_url`, `type`)
VALUES ('회원 가입', '피에스타 회원가입!', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge1.png', 'USER'),
       ('첫 리뷰 작성', '피에스타 첫 리뷰', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge2.png', 'REVIEW'),
       ('첫 등록', '피에스타 첫 등록', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge3.png', 'FESTIVAL'),
       ('열혈 참여러', '✨리뷰 5개 작성✨', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge4.png', 'REVIEW'),
       ('역사 탐방러', '리뷰 2개째!', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge5.png', 'REVIEW'),
       ('음악 탐방러', '리뷰 2개째!', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge6.png', 'REVIEW'),
       ('액티비티 탐방러', '리뷰 2개째!', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge7.png', 'REVIEW'),
       ('음식&술 탐방러', '리뷰 2개째!', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge8.png', 'REVIEW'),
       ('영화 탐방러', '리뷰 2개째!', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge9.png', 'REVIEW'),
       ('불꽃축제 탐방러', '리뷰 2개째!', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge10.png', 'REVIEW'),
       ('자연 탐방러', '리뷰 2개째!', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge11.png', 'REVIEW'),
       ('야간 탐방러', '리뷰 2개째!', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge12.png', 'REVIEW'),
       ('미술 탐방러', '리뷰 2개째!', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge13.png', 'REVIEW'),
       ('문화 탐방러', '리뷰 2개째!', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/badge14.png', 'REVIEW'),
       ('이색축제 탐방러', '리뷰 2개째!', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/badge/bade15.png', 'REVIEW');
