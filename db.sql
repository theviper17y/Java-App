CREATE DATABASE xss_db;

USE xss_db;

CREATE TABLE vulnerabilities (
    id INT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    payload VARCHAR(255) NOT NULL,
    found_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
