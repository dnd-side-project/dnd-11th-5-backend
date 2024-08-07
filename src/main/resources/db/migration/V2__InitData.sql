-- 고정 데이터 삽입

-- 페스티벌 관련 고정 데이터
INSERT INTO `category` (`category`)
VALUES ('문화'),
       ('음악&댄스'),
       ('영화'),
       ('음식&술'),
       ('스포츠&체험'),
       ('미술'),
       ('역사'),
       ('자연'),
       ('반려동물'),
       ('야간'),
       ('불꽃축제'),
       ('이색축제');

INSERT INTO `companion` (`companion_type`)
VALUES ('가족'),
       ('친구'),
       ('직장 동료'),
       ('연인'),
       ('혼자');

INSERT INTO `mood` (`mood`)
VALUES ('낭만적인'),
       ('여유로운'),
       ('활기찬'),
       ('모험적인'),
       ('화려한'),
       ('예술적인'),
       ('힙한'),
       ('감성적인'),
       ('레트로한'),
       ('친근한'),
       ('색다른'),
       ('로맨틱한'),
       ('클래식한'),
       ('신비한'),
       ('전통적인'),
       ('재미있는'),
       ('감동이 있는');

INSERT INTO `priority` (`priority`)
VALUES ('✅ 주제 관심사 일치'),
       ('💵 페스티벌 입장료'),
       ('🌭 페스티벌 내 음식 가격'),
       ('📒 주요 프로그램'),
       ('👭 동행하는 사람'),
       ('📅 날짜'),
       ('📍 위치'),
       ('📷 포토스팟');

-- 사용자 유형 데이터
INSERT INTO `user_type` (`name`, `profile_image`)
VALUES ('로맨티스트', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/user_type/user_type_1.png'),
       ('파티피플러', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/user_type/user_type_2.png'),
       ('인스파이어러', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/user_type/user_type_3.png'),
       ('몽글몽글 힐링러', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/user_type/user_type_4.png'),
       ('탐험러', 'https://fiesta-image.s3.ap-northeast-2.amazonaws.com/user_type/user_type_4.png');

-- 시도 행정 지역
INSERT INTO `Sido` (`sido`, `code`)
VALUES ('서울', 1),
       ('인천', 2),
       ('대전', 3),
       ('대구', 4),
       ('광주', 5),
       ('부산', 6),
       ('울산', 7),
       ('세종특별자치시', 8),
       ('경기도', 31),
       ('강원특별자치도', 32),
       ('충청북도', 33),
       ('충청남도', 34),
       ('경상북도', 35),
       ('경상남도', 36),
       ('전북특별자치도', 37),
       ('전라남도', 38),
       ('제주도', 39);

-- 리뷰, 활동 일지 키워드
INSERT INTO `keyword` (`keyword`)
VALUES ('✨ 쾌적해요'),
       ('👀 볼거리가 많아요'),
       ('😬 인파가 많아요'),
       ('👏🏻 혼잡하지 않아요'),
       ('💵 가성비가 좋아요'),
       ('😀 안내를 잘해줘요'),
       ('🎈 새로워요'),
       ('📸 포토스팟이 많아요'),
       ('🤗 또 가고 싶어요'),
       ('🚌 교통이 편리해요'),
       ('👍🏻 구성이 좋아요'),
       ('🍔 맛있는게 많아요'),
       ('🤩 독특해요'),
       ('🎉 프로그램이 다양해요'),
       ('🥳 재밌어요'),
       ('😍 화려해요'),
       ('💫 시설이 깨끗해요');
