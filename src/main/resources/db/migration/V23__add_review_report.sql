CREATE TABLE review_report
(
    review_report_id BIGINT              NOT NULL PRIMARY KEY,
    review_id        BIGINT              NOT NULL,
    user_id          BIGINT              NOT NULL,
    description      VARCHAR(500)        NOT NULL,
    is_pending       BIT(1)    DEFAULT 1 NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_review_report_review_id ON review_report (review_id);
CREATE INDEX idx_review_report_is_pending ON review_report (is_pending);
