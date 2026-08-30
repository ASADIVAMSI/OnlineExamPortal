-- Create the MySQL database used by backend/src/main/resources/application.properties.
CREATE DATABASE IF NOT EXISTS online_exam;
USE online_exam;

-- Users are approved by an administrator before they can take an assessment.
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(180) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    approval_status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
);

-- Each question belongs to one question set assigned by an administrator.
CREATE TABLE IF NOT EXISTS question_sets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL
);

-- Four-option programming questions are linked to their question set.
CREATE TABLE IF NOT EXISTS questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_set_id BIGINT NOT NULL,
    question_text VARCHAR(1000) NOT NULL,
    option_a VARCHAR(255) NOT NULL,
    option_b VARCHAR(255) NOT NULL,
    option_c VARCHAR(255) NOT NULL,
    option_d VARCHAR(255) NOT NULL,
    question_type VARCHAR(255) NOT NULL,
    correct_option VARCHAR(255),
    CONSTRAINT fk_questions_question_set
        FOREIGN KEY (question_set_id) REFERENCES question_sets(id)
);

-- A user can have one active assignment, while a question set can be assigned to many users.
CREATE TABLE IF NOT EXISTS assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    question_set_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING_APPROVAL',
    CONSTRAINT fk_assignments_user
        FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_assignments_question_set
        FOREIGN KEY (question_set_id) REFERENCES question_sets(id)
);

-- A submission stores the result summary; individual answers are stored below.
CREATE TABLE IF NOT EXISTS submissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    question_set_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED',
    submitted_at DATETIME(6) NOT NULL,
    total_marks INT NOT NULL,
    correct_answers INT NOT NULL,
    wrong_answers INT NOT NULL,
    CONSTRAINT fk_submissions_user
        FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_submissions_question_set
        FOREIGN KEY (question_set_id) REFERENCES question_sets(id)
);

-- These rows preserve each answer selected by the user for a submitted assessment.
CREATE TABLE IF NOT EXISTS user_answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    submission_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    selected_option VARCHAR(1) NOT NULL,
    CONSTRAINT fk_user_answers_submission
        FOREIGN KEY (submission_id) REFERENCES submissions(id),
    CONSTRAINT fk_user_answers_question
        FOREIGN KEY (question_id) REFERENCES questions(id)
);
