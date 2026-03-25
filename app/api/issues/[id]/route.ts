import { NextRequest, NextResponse } from 'next/server';
import { query } from '@/lib/db';

export async function GET(
  request: NextRequest,
  { params }: { params: Promise<{ id: string }> }
) {
  try {
    const { id } = await params;
    const result = await query(
      `SELECT ir.*, b.title as book_title, m.name as member_name, l.name as librarian_name
      FROM issue_records_5900 ir
      LEFT JOIN books_5900 b ON ir.book_id = b.id
      LEFT JOIN members_5900 m ON ir.member_id = m.id
      LEFT JOIN librarians_5900 l ON ir.librarian_id = l.id
       WHERE ir.id = $1`,
      [id]
    );
    
    if (result.rows.length === 0) {
      return NextResponse.json({ error: 'Issue not found' }, { status: 404 });
    }
    
    return NextResponse.json(result.rows[0]);
  } catch (error) {
    console.error('GET issue error:', error);
    return NextResponse.json({ error: 'Failed to fetch issue' }, { status: 500 });
  }
}

export async function PUT(
  request: NextRequest,
  { params }: { params: Promise<{ id: string }> }
) {
  try {
    const { id } = await params;
    const { return_date } = await request.json();
    
    const result = await query(
      `UPDATE issue_records_5900 
       SET return_date = $1, status = 'returned'
       WHERE id = $2
       RETURNING *`,
      [return_date, id]
    );
    
    if (result.rows.length === 0) {
      return NextResponse.json({ error: 'Issue not found' }, { status: 404 });
    }
    
    return NextResponse.json(result.rows[0]);
  } catch (error) {
    console.error('PUT issue error:', error);
    return NextResponse.json({ error: 'Failed to update issue' }, { status: 500 });
  }
}

export async function DELETE(
  request: NextRequest,
  { params }: { params: Promise<{ id: string }> }
) {
  try {
    const { id } = await params;
    
    const result = await query('DELETE FROM issue_records_5900 WHERE id = $1 RETURNING *', [id]);
    
    if (result.rows.length === 0) {
      return NextResponse.json({ error: 'Issue not found' }, { status: 404 });
    }
    
    return NextResponse.json({ message: 'Issue deleted successfully' });
  } catch (error) {
    console.error('DELETE issue error:', error);
    return NextResponse.json({ error: 'Failed to delete issue' }, { status: 500 });
  }
}
