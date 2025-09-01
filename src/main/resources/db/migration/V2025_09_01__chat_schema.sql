-- Chat schema: rooms, members, messages
CREATE TABLE IF NOT EXISTS chat_rooms (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  room_key VARCHAR(255) NOT NULL UNIQUE,
  created_at DATETIME(3) NOT NULL
);

CREATE TABLE IF NOT EXISTS chat_room_members (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  room_id BIGINT NOT NULL,
  actor_type ENUM('user','lawyer') NOT NULL,
  actor_id BIGINT NOT NULL,
  last_seen_at DATETIME(3) NULL,
  CONSTRAINT uk_member UNIQUE (room_id, actor_type, actor_id),
  CONSTRAINT fk_member_room FOREIGN KEY (room_id) REFERENCES chat_rooms(id) ON DELETE CASCADE
);
CREATE INDEX idx_member_lookup ON chat_room_members (room_id, actor_type, actor_id);

CREATE TABLE IF NOT EXISTS chat_messages (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  room_id BIGINT NOT NULL,
  author_type ENUM('user','lawyer') NOT NULL,
  author_id BIGINT NOT NULL,
  target_type ENUM('user','lawyer') NOT NULL,
  target_id BIGINT NOT NULL,
  text VARCHAR(2000) NOT NULL,
  created_at DATETIME(3) NOT NULL,
  CONSTRAINT fk_message_room FOREIGN KEY (room_id) REFERENCES chat_rooms(id) ON DELETE CASCADE
);
CREATE INDEX idx_messages_room_created ON chat_messages (room_id, created_at);

