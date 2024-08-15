-- 테이블 초기화
TRUNCATE TABLE `category`;

-- 카테고리에 이모지 칼럼 추가
ALTER TABLE `category`
    add emoji varchar(255) not null;

-- 기본 데이터 셋팅
INSERT INTO `category` (`category`, `emoji`)
VALUES ('문화', '🌏'),
       ('음악&댄스', '🎵'),
       ('영화', '🎬'),
       ('음식&술', '🍔'),
       ('스포츠&체험', '🔥'),
       ('미술', '🎨'),
       ('역사', '🎞'),
       ('자연', '🌿'),
       ('반려동물', '🐶'),
       ('야간', '🌕'),
       ('불꽃축제', '💫'),
       ('이색축제', '🤹🏻‍');
