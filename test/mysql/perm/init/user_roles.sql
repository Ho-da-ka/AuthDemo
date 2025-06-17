CREATE TABLE IF NOT EXISTS user_roles (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT,
  role_id INT,
  UNIQUE KEY uk_user_role (user_id)
);