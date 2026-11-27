CREATE TABLE roles (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(30) NOT NULL UNIQUE,
                       description VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       phone VARCHAR(40),
                       password_hash VARCHAR(255) NOT NULL,
                       status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                       failed_login_count INTEGER NOT NULL DEFAULT 0,
                       created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                       role_id BIGINT NOT NULL,
                       CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id)
) ENGINE=InnoDB;

CREATE TABLE technician_profile (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    speciality VARCHAR(120),
                                    service_zone VARCHAR(160),
                                    availability VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
                                    user_id BIGINT NOT NULL UNIQUE,
                                    CONSTRAINT fk_technician_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE login_attempt (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               attempted_at DATETIME(6) NOT NULL,
                               ip_address VARCHAR(64),
                               success BOOLEAN NOT NULL,
                               user_id BIGINT NOT NULL,
                               CONSTRAINT fk_login_attempt_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_login_attempt_user_time ON login_attempt(user_id, attempted_at);