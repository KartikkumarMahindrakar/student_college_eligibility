-- Course Eligibility Portal - initial schema
-- CHECK constraints (MySQL 8.0.16+) are defense-in-depth alongside Bean Validation upstream.

CREATE TABLE app_users (
    id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username       VARCHAR(50)  NOT NULL,
    password_hash  VARCHAR(100) NOT NULL,
    role           VARCHAR(20)  NOT NULL DEFAULT 'ROLE_ADMIN',
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_app_users_username UNIQUE (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE student_requests (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    student_name    VARCHAR(100) NOT NULL,
    age             TINYINT UNSIGNED NOT NULL,
    gender          VARCHAR(10)  NOT NULL,
    jee_qualified   TINYINT(1)   NOT NULL DEFAULT 0,
    neet_qualified  TINYINT(1)   NOT NULL DEFAULT 0,
    desired_course  VARCHAR(60)  NOT NULL,
    submitted_at    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    CONSTRAINT chk_student_requests_age CHECK (age BETWEEN 17 AND 25),
    CONSTRAINT chk_student_requests_gender CHECK (gender IN ('Male', 'Female', 'Other')),
    CONSTRAINT chk_student_requests_course CHECK (desired_course IN (
        'Computer Science Engineering', 'Mechanical Engineering', 'Electrical Engineering',
        'Civil Engineering', 'Electronics and Communication Engineering',
        'MBBS', 'BDS', 'BAMS', 'BHMS', 'BPT',
        'B.Com', 'BBA', 'BBM', 'CA',
        'BA in History', 'BA in Psychology', 'BA in Sociology', 'BA in Political Science', 'BA in English'
    ))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE INDEX idx_student_requests_name ON student_requests (student_name);
CREATE INDEX idx_student_requests_course ON student_requests (desired_course);
CREATE INDEX idx_student_requests_submitted_at ON student_requests (submitted_at);

CREATE TABLE subject_marks (
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    student_request_id  BIGINT UNSIGNED NOT NULL,
    subject_name        VARCHAR(30) NOT NULL,
    marks               TINYINT UNSIGNED NOT NULL,
    CONSTRAINT fk_subject_marks_student_request FOREIGN KEY (student_request_id)
        REFERENCES student_requests (id) ON DELETE CASCADE,
    CONSTRAINT uq_subject_marks_request_subject UNIQUE (student_request_id, subject_name),
    CONSTRAINT chk_subject_marks_marks CHECK (marks BETWEEN 0 AND 100),
    CONSTRAINT chk_subject_marks_subject CHECK (subject_name IN (
        'Physics', 'Chemistry', 'Mathematics', 'Biology', 'Accountancy', 'Business Studies',
        'Economics', 'History', 'Political Science', 'Geography', 'Psychology', 'Sociology', 'English'
    ))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE eligibility_results (
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    student_request_id  BIGINT UNSIGNED NOT NULL,
    eligible            TINYINT(1)  NOT NULL,
    reason              VARCHAR(500) NOT NULL,
    evaluated_at        DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_eligibility_results_student_request FOREIGN KEY (student_request_id)
        REFERENCES student_requests (id) ON DELETE CASCADE,
    CONSTRAINT uq_eligibility_results_student_request UNIQUE (student_request_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE INDEX idx_eligibility_results_eligible ON eligibility_results (eligible);
CREATE INDEX idx_eligibility_results_evaluated_at ON eligibility_results (evaluated_at);

CREATE TABLE recommended_courses (
    id                     BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    eligibility_result_id  BIGINT UNSIGNED NOT NULL,
    course_name            VARCHAR(60) NOT NULL,
    rank_order             TINYINT UNSIGNED NOT NULL,
    CONSTRAINT fk_recommended_courses_eligibility_result FOREIGN KEY (eligibility_result_id)
        REFERENCES eligibility_results (id) ON DELETE CASCADE,
    CONSTRAINT uq_recommended_courses_result_rank UNIQUE (eligibility_result_id, rank_order)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
