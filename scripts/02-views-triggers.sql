-- Views and Triggers for Library Management System

-- View 1: Current Issued Books (Books currently with members_5900)
CREATE OR REPLACE VIEW current_issued_books AS
SELECT 
  ir.id,
  ir.issue_date,
  ir.due_date,
  b.id as book_id,
  b.title,
  b.author,
  m.id as member_id,
  m.name as member_name,
  m.email as member_email,
  l.name as librarian_name,
  ir.status
FROM issue_records_5900 ir
JOIN books_5900 b ON ir.book_id = b.id
JOIN members_5900 m ON ir.member_id = m.id
JOIN librarians_5900 l ON ir.librarian_id = l.id
WHERE ir.status = 'issued'
ORDER BY ir.due_date ASC;

-- View 2: Books Summary (availability status)
CREATE OR REPLACE VIEW books_summary AS
SELECT 
  id,
  title,
  author,
  isbn,
  total_copies,
  available_copies,
  (total_copies - available_copies) as copies_issued,
  CASE 
    WHEN available_copies > 0 THEN 'Available'
    ELSE 'Out of Stock'
  END as availability_status
FROM books_5900
ORDER BY title ASC;

-- Trigger: Update book available_copies when issue_record is inserted
CREATE OR REPLACE FUNCTION update_book_copies_on_issue()
RETURNS TRIGGER AS $$
BEGIN
  IF NEW.status = 'issued' THEN
    UPDATE books_5900 SET available_copies = available_copies - 1 WHERE id = NEW.book_id;
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_update_book_copies_on_issue ON issue_records_5900;
CREATE TRIGGER trigger_update_book_copies_on_issue
AFTER INSERT ON issue_records_5900
FOR EACH ROW
EXECUTE FUNCTION update_book_copies_on_issue();

-- Trigger: Update book available_copies when return_date is set (book returned)
CREATE OR REPLACE FUNCTION update_book_copies_on_return()
RETURNS TRIGGER AS $$
BEGIN
  IF NEW.status = 'returned' AND OLD.status = 'issued' THEN
    UPDATE books_5900 SET available_copies = available_copies + 1 WHERE id = NEW.book_id;
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_update_book_copies_on_return ON issue_records_5900;
CREATE TRIGGER trigger_update_book_copies_on_return
AFTER UPDATE ON issue_records_5900
FOR EACH ROW
EXECUTE FUNCTION update_book_copies_on_return();

-- Trigger: Update updated_at timestamp
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = CURRENT_TIMESTAMP;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_update_books_timestamp ON books_5900;
CREATE TRIGGER trigger_update_books_timestamp
BEFORE UPDATE ON books_5900
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

DROP TRIGGER IF EXISTS trigger_update_members_timestamp ON members_5900;
CREATE TRIGGER trigger_update_members_timestamp
BEFORE UPDATE ON members_5900
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

DROP TRIGGER IF EXISTS trigger_update_librarians_timestamp ON librarians_5900;
CREATE TRIGGER trigger_update_librarians_timestamp
BEFORE UPDATE ON librarians_5900
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

DROP TRIGGER IF EXISTS trigger_update_issue_records_timestamp ON issue_records_5900;
CREATE TRIGGER trigger_update_issue_records_timestamp
BEFORE UPDATE ON issue_records_5900
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();
