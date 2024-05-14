CREATE TABLE users
(
    user_id   INT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL UNIQUE
);
CREATE TABLE messages
(
    message_id     INT AUTO_INCREMENT PRIMARY KEY,
    sender_id      INT  NOT NULL,
    receiver_id    INT  NOT NULL,
    message_body   TEXT NOT NULL,
    message_read_status TINYINT(1) DEFAULT 0,
    message_created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users (user_id) ON DELETE CASCADE
);
