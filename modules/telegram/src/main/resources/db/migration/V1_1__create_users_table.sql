CREATE TABLE users
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT       NOT NULL,
    first_name      VARCHAR(255) NOT NULL,
    user_name       VARCHAR(255),
    last_name       VARCHAR(255),
    league          VARCHAR(255),
    last_message_id INT
);
