CREATE TABLE users
(
    user_id   INT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE servers
(
    server_id INT AUTO_INCREMENT PRIMARY KEY,
    server_ip VARCHAR(50) NOT NULL,
    server_port INT NOT NULL,
    UNIQUE (server_ip, server_port)
);

CREATE TABLE conversations
(
    conversation_id         INT AUTO_INCREMENT PRIMARY_KEY,
    server_id               INT,
    conversation_name       VARCHAR(255) NOT NULL,
    conversation_created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (server_id) REFERENCES servers (server_id) ON DELETE SET NULL
);

CREATE TABLE messages
(
    message_id          INT AUTO_INCREMENT PRIMARY KEY,
    msg_conversation_id INT  NOT NULL,
    msg_sender_id       INT  NOT NULL,
    message_body        TEXT NOT NULL,
    message_read_status TINYINT(1) DEFAULT 0,
    message_created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (msg_conversation_id) REFERENCES conversations (conversation_id) ON DELETE CASCADE,
    FOREIGN KEY (msg_sender_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE conversation_participants
(
    cp_conversation_id INT NOT NULL,
    cp_user_id         INT NOT NULL,
    PRIMARY KEY (cp_conversation_id, cp_user_id),
    FOREIGN KEY (cp_conversation_id) REFERENCES conversations (conversation_id) ON DELETE CASCADE,
    FOREIGN KEY (cp_user_id) REFERENCES users (user_id) ON DELETE CASCADE
);
