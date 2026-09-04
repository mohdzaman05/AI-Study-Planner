-- ==========================================================
-- AI Study Planner - Relational Database Schema
-- Target: MySQL 8.0+ / MariaDB 10.5+
-- Compatible with H2 (MySQL Mode)
-- ==========================================================

-- Drop tables in reverse order of foreign key dependencies
DROP TABLE IF EXISTS study_tasks;
DROP TABLE IF EXISTS study_plans;
DROP TABLE IF EXISTS exams;
DROP TABLE IF EXISTS topics;
DROP TABLE IF EXISTS subjects;
DROP TABLE IF EXISTS users;

-- 1. Users Table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    daily_study_hours DECIMAL(3, 1) DEFAULT 3.0,
    preferred_study_time VARCHAR(20) DEFAULT '18:00',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. Subjects Table
CREATE TABLE subjects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    description TEXT,
    difficulty VARCHAR(20) NOT NULL DEFAULT 'MEDIUM', -- EASY, MEDIUM, HARD
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',   -- LOW, MEDIUM, HIGH
    exam_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_subjects_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Topics Table
CREATE TABLE topics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    difficulty VARCHAR(20) NOT NULL DEFAULT 'MEDIUM', -- EASY, MEDIUM, HARD
    estimated_minutes INT NOT NULL DEFAULT 45,
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED', -- NOT_STARTED, IN_PROGRESS, COMPLETED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_topics_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
);

-- 4. Exams Table
CREATE TABLE exams (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    exam_date DATE NOT NULL,
    exam_time TIME,
    important_topics TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_exams_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_exams_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
);

-- 5. Study Plans Table
CREATE TABLE study_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    available_hours_per_day DECIMAL(3, 1) NOT NULL,
    preferred_start_time VARCHAR(20) DEFAULT '18:00',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, ARCHIVED
    prompt_summary TEXT,
    CONSTRAINT fk_plans_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 6. Study Tasks Table
CREATE TABLE study_tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    study_plan_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    topic_id BIGINT,
    task_date DATE NOT NULL,
    start_time VARCHAR(20) NOT NULL,
    duration_minutes INT NOT NULL,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM', -- LOW, MEDIUM, HIGH
    reason_recommendation TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',  -- PENDING, COMPLETED, MISSED
    completed_at TIMESTAMP NULL,
    CONSTRAINT fk_tasks_plan FOREIGN KEY (study_plan_id) REFERENCES study_plans(id) ON DELETE CASCADE,
    CONSTRAINT fk_tasks_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    CONSTRAINT fk_tasks_topic FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE SET NULL
);

-- Indexes for performance & query optimization
CREATE INDEX idx_subjects_user ON subjects(user_id);
CREATE INDEX idx_topics_subject ON topics(subject_id);
CREATE INDEX idx_exams_user ON exams(user_id);
CREATE INDEX idx_exams_date ON exams(exam_date);
CREATE INDEX idx_plans_user ON study_plans(user_id);
CREATE INDEX idx_tasks_plan ON study_tasks(study_plan_id);
CREATE INDEX idx_tasks_date ON study_tasks(task_date);
CREATE INDEX idx_tasks_status ON study_tasks(status);

-- ==========================================================
-- Optional Seed / Demo Data
-- Password for demo@example.com is: Demo@12345
-- BCrypt hash: $2a$10$3Ym6l16/8hDq5lK.1kox0.qMhDqC56W8.8lYvA2cE8YjE2oY1vM2y
-- ==========================================================
INSERT INTO users (id, full_name, email, password_hash, daily_study_hours, preferred_study_time)
VALUES (1, 'Demo Student', 'demo@example.com', '$2a$10$w8T0M4j5vGf5UoG.2p1qOeZ/H207l1F9gW1j0gR9bU7IuVzXz3iKO', 3.5, '17:00');
