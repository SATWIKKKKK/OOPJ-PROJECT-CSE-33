-- ============================================================
--  COLLEGE ADMISSION AND FEE MANAGEMENT SYSTEM
--  PostgreSQL Schema
--  Safe to re-run: uses IF NOT EXISTS, does NOT drop existing data
-- ============================================================

-- ── COURSES ───────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS courses (
    course_id               VARCHAR(10)    PRIMARY KEY,
    course_name             VARCHAR(100)   NOT NULL,
    course_type             VARCHAR(20)    NOT NULL CHECK (course_type IN ('UG','PG','DIPLOMA')),
    duration_years          INT            NOT NULL,
    total_seats             INT            NOT NULL,
    available_seats         INT            NOT NULL,
    min_percentage_required NUMERIC(5,2)   NOT NULL,
    department              VARCHAR(60)    NOT NULL,
    description             TEXT,
    created_at              TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
);

-- ── FEE STRUCTURES ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS fee_structures (
    course_id                  VARCHAR(10)   PRIMARY KEY REFERENCES courses(course_id) ON DELETE CASCADE,
    course_name                VARCHAR(100)  NOT NULL,
    admission_fee              NUMERIC(10,2) NOT NULL,
    tuition_fee_per_semester   NUMERIC(10,2) NOT NULL,
    library_fee_per_semester   NUMERIC(10,2) NOT NULL,
    lab_fee_per_semester       NUMERIC(10,2) NOT NULL,
    exam_fee_per_semester      NUMERIC(10,2) NOT NULL,
    hostel_fee_per_year        NUMERIC(10,2) NOT NULL,
    transport_fee_per_year     NUMERIC(10,2) NOT NULL
);

-- ── APPLICANTS ────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS applicants (
    application_id         VARCHAR(15)   PRIMARY KEY,
    full_name              VARCHAR(100)  NOT NULL,
    email                  VARCHAR(100)  NOT NULL,
    phone                  VARCHAR(15)   NOT NULL,
    address                TEXT,
    gender                 VARCHAR(10),
    date_of_birth          VARCHAR(20),
    applied_course_id      VARCHAR(10)   REFERENCES courses(course_id),
    applied_course_name    VARCHAR(100),
    previous_institution   VARCHAR(150),
    previous_percentage    NUMERIC(5,2)  NOT NULL,
    application_date       DATE          DEFAULT CURRENT_DATE,
    application_status     VARCHAR(15)   DEFAULT 'PENDING' CHECK (application_status IN ('PENDING','APPROVED','REJECTED')),
    remarks                TEXT          DEFAULT '-'
);

-- ── STUDENTS ──────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS students (
    student_id       VARCHAR(15)   PRIMARY KEY,
    full_name        VARCHAR(100)  NOT NULL,
    email            VARCHAR(100)  NOT NULL,
    phone            VARCHAR(15),
    address          TEXT,
    gender           VARCHAR(10),
    date_of_birth    VARCHAR(20),
    enrolled_course  VARCHAR(100),
    course_id        VARCHAR(10)   REFERENCES courses(course_id),
    semester         INT           DEFAULT 1,
    cgpa             NUMERIC(4,2)  DEFAULT 0.0,
    admission_year   VARCHAR(10),
    status           VARCHAR(15)   DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','INACTIVE','GRADUATED')),
    enrolled_at      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

-- ── PAYMENTS ──────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS payments (
    payment_id      VARCHAR(15)   PRIMARY KEY,
    student_id      VARCHAR(15)   REFERENCES students(student_id),
    student_name    VARCHAR(100),
    course_id       VARCHAR(10),
    amount          NUMERIC(10,2) NOT NULL,
    payment_date    DATE          DEFAULT CURRENT_DATE,
    payment_type    VARCHAR(20)   CHECK (payment_type IN ('ADMISSION','TUITION','HOSTEL','TRANSPORT','EXAM')),
    payment_method  VARCHAR(10)   CHECK (payment_method IN ('CASH','ONLINE','CHEQUE','DD')),
    semester        VARCHAR(10),
    transaction_id  VARCHAR(30)   UNIQUE,
    status          VARCHAR(10)   DEFAULT 'SUCCESS'
);

-- ── INDEXES ───────────────────────────────────────────────────────────────────
CREATE INDEX idx_applicants_status    ON applicants(application_status);
CREATE INDEX idx_students_course      ON students(course_id);
CREATE INDEX idx_payments_student     ON payments(student_id);
CREATE INDEX idx_payments_type        ON payments(payment_type);

-- ── VERIFY (optional) ────────────────────────────────────────────────────────
SELECT 'Tables ready. Courses: ' || COUNT(*) FROM courses;
SELECT 'Fee structures: '        || COUNT(*) FROM fee_structures;
