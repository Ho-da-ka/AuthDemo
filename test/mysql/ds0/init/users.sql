-- 针对 user_db_0 / user_db_1，各执行一次
CREATE TABLE IF NOT EXISTS users_0 (
  user_id BIGINT PRIMARY KEY,
  username VARCHAR(50),
  password VARCHAR(255),
  email VARCHAR(100),
  phone VARCHAR(20),
  gmt_create TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS users_1 LIKE users_0;