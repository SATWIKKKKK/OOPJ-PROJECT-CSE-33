erDiagram
  books_5900 {
    id integer
    title character_varying
    author character_varying
    isbn character_varying
    total_copies integer
    available_copies integer
    publication_year integer
    category character_varying
    created_at timestamp_without_time_zone
    updated_at timestamp_without_time_zone
  }
  issue_records_5900 {
    id integer
    book_id integer
    member_id integer
    librarian_id integer
    issue_date date
    due_date date
    return_date date
    status character_varying
    created_at timestamp_without_time_zone
    updated_at timestamp_without_time_zone
  }
  librarians_5900 {
    id integer
    name character_varying
    email character_varying
    phone character_varying
    employee_id character_varying
    hire_date date
    created_at timestamp_without_time_zone
    updated_at timestamp_without_time_zone
  }
  members_5900 {
    id integer
    name character_varying
    email character_varying
    phone character_varying
    address text
    membership_date date
    status character_varying
    created_at timestamp_without_time_zone
    updated_at timestamp_without_time_zone
  }
  books_5900 ||--o{ issue_records_5900 : "book_id -> id"
  librarians_5900 ||--o{ issue_records_5900 : "librarian_id -> id"
  members_5900 ||--o{ issue_records_5900 : "member_id -> id"
