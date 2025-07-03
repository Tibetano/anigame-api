CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE tb_users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE tb_roles (
    role_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE tb_refresh_tokens (
    refresh_token_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    expires_at TIMESTAMPTZ(6) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES tb_users(user_id) ON DELETE CASCADE
);

CREATE TABLE tb_users_roles (
    user_id UUID NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES tb_users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES tb_roles(role_id) ON DELETE CASCADE
);