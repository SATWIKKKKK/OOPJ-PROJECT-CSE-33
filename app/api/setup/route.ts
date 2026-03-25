import { NextRequest, NextResponse } from 'next/server';
import { query } from '@/lib/db';

export async function POST(request: NextRequest) {
  try {
    // Seed initial data
    const queries = [
      `INSERT INTO books_5900 (title, author, isbn, publication_year, total_copies, available_copies)
       VALUES ('The Great Gatsby', 'F. Scott Fitzgerald', '978-0743273565', 1925, 5, 5),
              ('To Kill a Mockingbird', 'Harper Lee', '978-0061120084', 1960, 4, 4),
              ('1984', 'George Orwell', '978-0451524935', 1949, 3, 3),
              ('Pride and Prejudice', 'Jane Austen', '978-0141439518', 1813, 6, 6),
              ('The Catcher in the Rye', 'J.D. Salinger', '978-0316769174', 1951, 2, 2)
       ON CONFLICT (isbn) DO NOTHING`,
      
      `INSERT INTO members_5900 (name, email, phone, address, membership_date)
       VALUES ('Alice Johnson', 'alice@example.com', '555-0101', '123 Main St', NOW()),
              ('Bob Smith', 'bob@example.com', '555-0102', '456 Oak Ave', NOW()),
              ('Carol White', 'carol@example.com', '555-0103', '789 Pine Rd', NOW()),
              ('David Brown', 'david@example.com', '555-0104', '321 Elm St', NOW())
       ON CONFLICT (email) DO NOTHING`,
      
      `INSERT INTO librarians_5900 (name, email, phone, employee_id, hire_date)
       VALUES ('Emma Davis', 'emma@library.com', '555-0201', 'LIB001', NOW()),
              ('Frank Miller', 'frank@library.com', '555-0202', 'LIB002', NOW())
       ON CONFLICT (employee_id) DO NOTHING`
    ];

    for (const q of queries) {
      await query(q);
    }

    return NextResponse.json({ message: 'Database setup completed successfully' }, { status: 200 });
  } catch (error) {
    console.error('Setup error:', error);
    return NextResponse.json({ error: 'Setup failed' }, { status: 500 });
  }
}
