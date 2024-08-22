DROP TABLE IF EXISTS `local_user`;
DROP TABLE IF EXISTS `oauth_user`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `user_role`;

-- Base User Table
CREATE TABLE users
(
    user_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    email          VARCHAR(320) UNIQUE,
    user_type_id   BIGINT,
    nickname       VARCHAR(10) NOT NULL,
    status_message VARCHAR(50),
    profile_image  VARCHAR(255),
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE user_role
(
    user_role_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id      BIGINT NOT NULL,
    role_id      BIGINT NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
