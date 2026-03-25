-- ============================================================
--  COLLEGE ADMISSION AND FEE MANAGEMENT SYSTEM
--  MySQL Database Schema
--  Run this file FIRST before starting the Java application
-- ============================================================

-- Step 1: Create and use the database
CREATE DATABASE IF NOT EXISTS college_db;
USE college_db;

-- ============================================================
--  TABLE: courses
-- ============================================================
CREATE TABLE IF NOT EXISTS courses (
    course_id           VARCHAR(10)    PRIMARY KEY,
    course_name         VARCHAR(100)   NOT NULL,
    course_type         VARCHAR(20)    NOT NULL,   -- UG | PG | DIPLOMA
    duration            INT            NOT NULL,   -- in years
    total_seats         INT            NOT NULL,
    available_seats     INT            NOT NULL,
    min_percentage      DOUBLE         NOT NULL,
    department          VARCHAR(100)   NOT NULL,
    description         VARCHAR(255)
);

-- ============================================================
--  TABLE: fee_structures
-- ============================================================
CREATE TABLE IF NOT EXISTS fee_structures (
    course_id               VARCHAR(10)    PRIMARY KEY,
    course_name             VARCHAR(100)   NOT NULL,
    admission_fee           DOUBLE         NOT NULL,
    tuition_fee_per_sem     DOUBLE         NOT NULL,
    library_fee_per_sem     DOUBLE         NOT NULL,
    lab_fee_per_sem         DOUBLE         NOT NULL,
    exam_fee_per_sem        DOUBLE         NOT NULL,
    hostel_fee_per_year     DOUBLE         NOT NULL,
    transport_fee_per_year  DOUBLE         NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

-- ============================================================
--  TABLE: applicants
-- ============================================================
CREATE TABLE IF NOT EXISTS applicants (
    application_id          VARCHAR(15)   PRIMARY KEY,
    name                    VARCHAR(100)  NOT NULL,
    email                   VARCHAR(100)  NOT NULL,
    phone                   VARCHAR(20)   NOT NULL,
    address                 VARCHAR(255),
    gender                  VARCHAR(10),
    date_of_birth           VARCHAR(20),
    applied_course_id       VARCHAR(10)   NOT NULL,
    applied_course_name     VARCHAR(100)  NOT NULL,
    previous_institution    VARCHAR(150),
    previous_percentage     DOUBLE        NOT NULL,
    application_date        VARCHAR(20)   NOT NULL,
    application_status      VARCHAR(20)   DEFAULT 'PENDING',   -- PENDING | APPROVED | REJECTED
    remarks                 VARCHAR(255)  DEFAULT '-',
    FOREIGN KEY (applied_course_id) REFERENCES courses(course_id)
);

-- ============================================================
--  TABLE: students
-- ============================================================
CREATE TABLE IF NOT EXISTS students (
    student_id              VARCHAR(15)   PRIMARY KEY,
    person_id               VARCHAR(20),
    name                    VARCHAR(100)  NOT NULL,
    email                   VARCHAR(100)  NOT NULL,
    phone                   VARCHAR(20),
    address                 VARCHAR(255),
    gender                  VARCHAR(10),
    date_of_birth           VARCHAR(20),
    enrolled_course         VARCHAR(100)  NOT NULL,
    course_id               VARCHAR(10)   NOT NULL,
    semester                INT           DEFAULT 1,
    cgpa                    DOUBLE        DEFAULT 0.0,
    admission_year          VARCHAR(10),
    status                  VARCHAR(20)   DEFAULT 'ACTIVE',    -- ACTIVE | INACTIVE | GRADUATED
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

-- ============================================================
--  TABLE: payments
-- ============================================================
CREATE TABLE IF NOT EXISTS payments (
    payment_id              VARCHAR(15)   PRIMARY KEY,
    student_id              VARCHAR(15)   NOT NULL,
    student_name            VARCHAR(100)  NOT NULL,
    course_id               VARCHAR(10)   NOT NULL,
    amount                  DOUBLE        NOT NULL,
    payment_date            VARCHAR(20)   NOT NULL,
    payment_type            VARCHAR(30)   NOT NULL,  -- TUITION | HOSTEL | EXAM | ADMISSION | TRANSPORT
    payment_method          VARCHAR(20)   NOT NULL,  -- CASH | ONLINE | CHEQUE | DD
    semester                VARCHAR(15),
    transaction_id          VARCHAR(30)   NOT NULL,
    status                  VARCHAR(15)   DEFAULT 'SUCCESS',
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);

-- ============================================================
--  SEED DATA: Courses — ADD YOUR OWN VIA Admin > Add New Course
-- ============================================================
-- (No pre-loaded dummy data. Use the Admin panel to add courses.)

-- ============================================================
--  Useful queries for manual inspection
-- ============================================================
-- SELECT * FROM courses;
-- SELECT * FROM applicants;
-- SELECT * FROM students;
-- SELECT * FROM payments;
-- SELECT a.application_id, a.name, a.applied_course_name, a.application_status FROM applicants a;
-- SELECT s.student_id, s.name, s.enrolled_course, s.semester, s.status FROM students s;
-- SELECT SUM(amount) AS total_collected FROM payments WHERE status='SUCCESS';
