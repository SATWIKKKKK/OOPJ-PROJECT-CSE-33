import { NextRequest, NextResponse } from 'next/server';
import { query } from '@/lib/db';

export async function GET(request: NextRequest) {
  try {
    const result = await query('SELECT * FROM books_5900 ORDER BY title ASC');
    return NextResponse.json(result.rows);
  } catch (error) {
    console.error('GET books error:', error);
    return NextResponse.json({ error: 'Failed to fetch books' }, { status: 500 });
  }
}

export async function POST(request: NextRequest) {
  try {
    const { title, author, isbn, publication_year, total_copies } = await request.json();
    
    const result = await query(
      `INSERT INTO books_5900 (title, author, isbn, publication_year, total_copies, available_copies)
       VALUES ($1, $2, $3, $4, $5, $5)
       RETURNING *`,
      [title, author, isbn, publication_year, total_copies]
    );
    
    return NextResponse.json(result.rows[0], { status: 201 });
  } catch (error) {
    console.error('POST books error:', error);
    return NextResponse.json({ error: 'Failed to create book' }, { status: 500 });
  }
}
