CREATE TABLE books
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    title         VARCHAR(200)          NOT NULL,
    `description` VARCHAR(1000)         NULL,
    genre         VARCHAR(50)           NULL,
    author_id     BIGINT                NOT NULL,
    CONSTRAINT pk_books PRIMARY KEY (id)
);

ALTER TABLE books
    ADD CONSTRAINT FK_BOOKS_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES authors (author_id);