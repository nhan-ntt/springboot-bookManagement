CREATE TABLE authors
(
    author_id BIGINT AUTO_INCREMENT NOT NULL,
    full_name VARCHAR(100)          NOT NULL,
    biography VARCHAR(500)          NULL,
    CONSTRAINT pk_authors PRIMARY KEY (author_id)
);