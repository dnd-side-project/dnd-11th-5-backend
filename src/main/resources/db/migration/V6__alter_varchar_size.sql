-- `homepage_url` 칼럼을 VARCHAR(1024)로 변경
ALTER TABLE `festival`
    MODIFY COLUMN `homepage_url` VARCHAR(1024);

-- `instagram_url` 칼럼을 VARCHAR(1024)로 변경
ALTER TABLE `festival`
    MODIFY COLUMN `instagram_url` VARCHAR(1024);

-- `ticket_link` 칼럼을 VARCHAR(1024)로 변경
ALTER TABLE `festival`
    MODIFY COLUMN `ticket_link` VARCHAR(1024);

-- `profile_image` 칼럼을 VARCHAR(1024)로 변경
ALTER TABLE `user`
    MODIFY COLUMN `profile_image` VARCHAR(1024);