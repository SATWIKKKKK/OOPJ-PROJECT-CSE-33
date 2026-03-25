-- Library Management System Database Schema
-- PostgreSQL with Neon

-- Books Table
CREATE TABLE IF NOT EXISTS books_5900 (
  id SERIAL PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  author VARCHAR(255) NOT NULL,
  isbn VARCHAR(20) UNIQUE NOT NULL,
  total_copies INT NOT NULL DEFAULT 1,
  available_copies INT NOT NULL DEFAULT 1,
  publication_year INT,
  category VARCHAR(100),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Members Table
CREATE TABLE IF NOT EXISTS members_5900 (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  phone VARCHAR(20),
  address TEXT,
  membership_date DATE NOT NULL DEFAULT CURRENT_DATE,
  status VARCHAR(50) DEFAULT 'active',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Librarians Table
CREATE TABLE IF NOT EXISTS librarians_5900 (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  phone VARCHAR(20),
  employee_id VARCHAR(20) UNIQUE NOT NULL,
  hire_date DATE NOT NULL DEFAULT CURRENT_DATE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Issue Records Table
CREATE TABLE IF NOT EXISTS issue_records_5900 (
  id SERIAL PRIMARY KEY,
  book_id INT NOT NULL REFERENCES books_5900(id) ON DELETE CASCADE,
  member_id INT NOT NULL REFERENCES members_5900(id) ON DELETE CASCADE,
  librarian_id INT NOT NULL REFERENCES librarians_5900(id),
  issue_date DATE NOT NULL DEFAULT CURRENT_DATE,
  due_date DATE NOT NULL,
  return_date DATE,
  status VARCHAR(50) DEFAULT 'issued',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Indexes for Performance
CREATE INDEX IF NOT EXISTS idx_issue_records_book_id ON issue_records_5900(book_id);
CREATE INDEX IF NOT EXISTS idx_issue_records_member_id ON issue_records_5900(member_id);
CREATE INDEX IF NOT EXISTS idx_issue_records_librarian_id ON issue_records_5900(librarian_id);
CREATE INDEX IF NOT EXISTS idx_issue_records_status ON issue_records_5900(status);
CREATE INDEX IF NOT EXISTS idx_books_isbn ON books_5900(isbn);
CREATE INDEX IF NOT EXISTS idx_members_email ON members_5900(email);
CREATE INDEX IF NOT EXISTS idx_librarians_email ON librarians_5900(email);
CREATE INDEX IF NOT EXISTS idx_librarians_employee_id ON librarians_5900(employee_id);
