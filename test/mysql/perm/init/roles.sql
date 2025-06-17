CREATE TABLE IF NOT EXISTS roles (
  role_id INT PRIMARY KEY,
  role_code VARCHAR(20) UNIQUE
);
INSERT IGNORE INTO roles VALUES
  (1,'super_admin'),(2,'user'),(3,'admin');