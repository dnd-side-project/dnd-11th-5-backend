DROP TABLE IF EXISTS `user_type`;

CREATE TABLE user_type
(
    user_type_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255),
    profile_image VARCHAR(2048),
    card_image    VARCHAR(2048),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


INSERT INTO user_type (name, profile_image, card_image)
VALUES ('로맨티스트', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/user_type/profile/user_romantic.png',
        'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/user_type/card/user-romantic.png'),
       ('파티피플러', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/user_type/profile/user-party.png',
        'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/user_type/card/user-party.png'),
       ('인스파이어러', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/user_type/profile/user-inspire.png',
        'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/user_type/card/user-inspire.png'),
       ('몽글몽글 힐링러', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/user_type/profile/user-healing.png',
        'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/user_type/card/user-healing.png'),
       ('호기심만땅 탐험러', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/user_type/profile/user-explore.png',
        'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/user_type/card/user-explore.png');
