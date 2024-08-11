ALTER TABLE `log`
    ADD `title` varchar(30) NOT NULL;
ALTER TABLE `log`
    ADD `rating` tinyint NOT NULL;
ALTER TABLE `log`
    ADD `address` varchar(255) NOT NULL;

ALTER TABLE `log_image`
    MODIFY `image_url` varchar(2083) NOT NULL;
