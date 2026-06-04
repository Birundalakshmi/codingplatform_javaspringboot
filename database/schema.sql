-- Live Coding Arena Database Schema
-- MySQL 8.0+

CREATE DATABASE IF NOT EXISTS livecoding_arena;
USE livecoding_arena;

-- Users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'MENTOR', 'ADMIN') DEFAULT 'STUDENT',
    total_score INT DEFAULT 0,
    problems_solved INT DEFAULT 0,
    score INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_total_score (total_score DESC)
);

-- Problems table
CREATE TABLE problems (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    difficulty ENUM('EASY', 'MEDIUM', 'HARD') NOT NULL,
    category VARCHAR(100),
    test_cases TEXT,
    expected_output TEXT,
    max_score INT DEFAULT 100,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_difficulty (difficulty),
    INDEX idx_category (category),
    INDEX idx_created_at (created_at DESC)
);

-- Submissions table
CREATE TABLE submissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    problem_id BIGINT NOT NULL,
    code TEXT NOT NULL,
    language VARCHAR(20) DEFAULT 'java',
    status ENUM('PENDING', 'ACCEPTED', 'WRONG_ANSWER', 'COMPILATION_ERROR', 'RUNTIME_ERROR', 'TIME_LIMIT_EXCEEDED') DEFAULT 'PENDING',
    score INT DEFAULT 0,
    execution_time INT DEFAULT 0,
    memory_used INT DEFAULT 0,
    test_cases_passed INT DEFAULT 0,
    total_test_cases INT DEFAULT 0,
    error_message TEXT,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_problem_id (problem_id),
    INDEX idx_status (status),
    INDEX idx_submitted_at (submitted_at DESC)
);

-- Code Sessions table for real-time collaboration
CREATE TABLE code_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(100) UNIQUE NOT NULL,
    problem_id BIGINT,
    created_by BIGINT NOT NULL,
    code TEXT,
    language VARCHAR(20) DEFAULT 'java',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_session_id (session_id),
    INDEX idx_created_by (created_by),
    INDEX idx_is_active (is_active)
);

-- Session Participants table
CREATE TABLE session_participants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (session_id) REFERENCES code_sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_session_user (session_id, user_id),
    INDEX idx_session_id (session_id),
    INDEX idx_user_id (user_id)
);

-- Sample data insertion
INSERT INTO users (username, email, password, role, total_score, problems_solved) VALUES
('admin', 'admin@livecoding.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ADMIN', 0, 0),
('mentor1', 'mentor1@livecoding.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'MENTOR', 250, 5),
('student1', 'student1@livecoding.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'STUDENT', 150, 3);

INSERT INTO problems (title, description, difficulty, category, test_cases, expected_output, max_score) VALUES
('Two Sum', 'Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.', 'EASY', 'Array', '[2,7,11,15]\n9', '[0,1]', 100),
('Reverse String', 'Write a function that reverses a string. The input string is given as an array of characters s.', 'EASY', 'String', '["h","e","l","l","o"]', '["o","l","l","e","h"]', 100),
('Valid Parentheses', 'Given a string s containing just the characters ''('', '')'', ''{'', ''}'', ''['' and '']'', determine if the input string is valid.', 'MEDIUM', 'Stack', '()[]{}', 'true', 150),
('Merge Two Sorted Lists', 'You are given the heads of two sorted linked lists list1 and list2. Merge the two lists in a sorted list.', 'MEDIUM', 'Linked List', '[1,2,4]\n[1,3,4]', '[1,1,2,3,4,4]', 150),
('Binary Tree Maximum Path Sum', 'A path in a binary tree is a sequence of nodes where each pair of adjacent nodes in the sequence has an edge connecting them.', 'HARD', 'Tree', '[1,2,3]', '6', 200);