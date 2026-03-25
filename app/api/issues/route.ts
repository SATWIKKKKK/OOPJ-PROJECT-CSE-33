import { NextRequest, NextResponse } from 'next/server';
import { query } from '@/lib/db';

export async function GET(request: NextRequest) {
  try {
    const result = await query(
      `SELECT ir.*, b.title as book_title, m.name as member_name, l.name as librarian_name
       FROM issue_records_5900 ir
       LEFT JOIN books_5900 b ON ir.book_id = b.id
       LEFT JOIN members_5900 m ON ir.member_id = m.id
       LEFT JOIN librarians_5900 l ON ir.librarian_id = l.id
       ORDER BY ir.issue_date DESC`
    );
    return NextResponse.json(result.rows);
  } catch (error) {
    console.error('GET issues error:', error);
    return NextResponse.json({ error: 'Failed to fetch issues' }, { status: 500 });
  }
}

export async function POST(request: NextRequest) {
  try {
    const { book_id, member_id, librarian_id, due_date } = await request.json();
    
    const result = await query(
      `INSERT INTO issue_records_5900 (book_id, member_id, librarian_id, issue_date, due_date, status)
       VALUES ($1, $2, $3, CURRENT_DATE, $4, 'issued')
       RETURNING *`,
      [book_id, member_id, librarian_id, due_date]
    );
    
    return NextResponse.json(result.rows[0], { status: 201 });
  } catch (error) {
    console.error('POST issues error:', error);
    return NextResponse.json({ error: 'Failed to create issue record' }, { status: 500 });
  }
}
