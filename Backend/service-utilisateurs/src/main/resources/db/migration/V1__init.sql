CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL UNIQUE,
    description VARCHAR(255)
);
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(40),
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    failed_login_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    role_id BIGINT NOT NULL REFERENCES roles(id)
);
CREATE UNIQUE INDEX idx_users_email ON users(email);
CREATE TABLE technician_profile (
    id BIGSERIAL PRIMARY KEY,
    speciality VARCHAR(120),
    service_zone VARCHAR(160),
    availability VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE
);
CREATE TABLE login_attempt (
    id BIGSERIAL PRIMARY KEY,
    attempted_at TIMESTAMP NOT NULL,
    ip_address VARCHAR(64),
    success BOOLEAN NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);
CREATE INDEX idx_login_attempt_user_time ON login_attempt(user_id, attempted_at);
