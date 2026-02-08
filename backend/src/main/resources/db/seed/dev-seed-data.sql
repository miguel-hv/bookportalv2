-- Docker Development Seed Data
-- This script runs automatically when containers start
-- It creates the schema and inserts fresh seed data

-- Create tables if they don't exist (for initial setup)
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(20) NOT NULL,
    author VARCHAR(255),
    review VARCHAR(200),
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Clear existing data (CASCADE handles foreign keys)
TRUNCATE TABLE books, user_roles, users, roles CASCADE;

-- Reset sequences for clean ID numbering
ALTER SEQUENCE IF EXISTS users_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS books_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS roles_id_seq RESTART WITH 1;

-- Insert roles
INSERT INTO roles (name) VALUES ('ROLE_ADMIN'), ('ROLE_USER');

-- Insert admin user (BCrypt hashed password: Password1!)
INSERT INTO users (username, password) 
VALUES ('admin', '$2a$10$4OynpGPk.9tGJmym7hhFNu5YGJj4gRayY02p7ZTMK4XI63/FOMtMO');

-- Insert regular user (BCrypt hashed password: Password1!)
INSERT INTO users (username, password) 
VALUES ('user', '$2a$10$4OynpGPk.9tGJmym7hhFNu5YGJj4gRayY02p7ZTMK4XI63/FOMtMO');

-- Assign roles to admin
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';

-- Assign roles to user
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'user' AND r.name = 'ROLE_USER';

-- Insert book for admin (1 book)
INSERT INTO books (title, author, review, user_id)
SELECT 'The Great Gatsby', 'F. Scott Fitzgerald', 'A classic American novel about the Jazz Age.', id
FROM users WHERE username = 'admin';

-- Insert books for user (2 books)
INSERT INTO books (title, author, review, user_id)
SELECT '1984', 'George Orwell', 'A dystopian social science fiction novel.', id
FROM users WHERE username = 'user';

INSERT INTO books (title, author, review, user_id)
SELECT 'Mockingbird', 'Harper Lee', 'A novel about racial injustice in the American South.', id
FROM users WHERE username = 'user';