CREATE TABLE festival_modification_request
(
    festival_modification_id BIGINT              NOT NULL PRIMARY KEY,
    festival_id              BIGINT              NOT NULL,
    user_id                  BIGINT              NOT NULL,
    content                  VARCHAR(500)        NOT NULL,
    is_pending               BIT(1)    DEFAULT 1 NOT NULL,
    created_at               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at               TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
