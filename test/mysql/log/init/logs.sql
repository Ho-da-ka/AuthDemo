CREATE TABLE IF NOT EXISTS operation_logs (
  log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT,
  action VARCHAR(50),
  ip VARCHAR(15),
  detail TEXT
);