--CREATE EXTENSION IF NOT EXISTS "pgcrypto";

--ALTER TABLE tb_users
--ALTER COLUMN user_id SET DEFAULT gen_random_uuid();

--INSERT INTO tb_roles (role_id, name) VALUES (1,'ADMIN') ON CONFLICT (role_id) DO NOTHING;
--INSERT INTO tb_roles (role_id, name) VALUES (2,'BASIC') ON CONFLICT (role_id) DO NOTHING;

INSERT INTO tb_roles (name) VALUES ('ADMIN') ON CONFLICT (name) DO NOTHING;
INSERT INTO tb_roles (name) VALUES ('BASIC') ON CONFLICT (name) DO NOTHING;