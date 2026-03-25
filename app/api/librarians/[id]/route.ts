import { NextRequest, NextResponse } from 'next/server';
import { query } from '@/lib/db';

export async function GET(
  request: NextRequest,
  { params }: { params: Promise<{ id: string }> }
) {
  try {
    const { id } = await params;
    const result = await query('SELECT * FROM librarians_5900 WHERE id = $1', [id]);
    
    if (result.rows.length === 0) {
      return NextResponse.json({ error: 'Librarian not found' }, { status: 404 });
    }
    
    return NextResponse.json(result.rows[0]);
  } catch (error) {
    console.error('GET librarian error:', error);
    return NextResponse.json({ error: 'Failed to fetch librarian' }, { status: 500 });
  }
}

export async function PUT(
  request: NextRequest,
  { params }: { params: Promise<{ id: string }> }
) {
  try {
    const { id } = await params;
    const { name, email, phone, employee_id } = await request.json();
    
    const result = await query(
      `UPDATE librarians_5900 
       SET name = $1, email = $2, phone = $3, employee_id = $4
       WHERE id = $5
       RETURNING *`,
      [name, email, phone, employee_id, id]
    );
    
    if (result.rows.length === 0) {
      return NextResponse.json({ error: 'Librarian not found' }, { status: 404 });
    }
    
    return NextResponse.json(result.rows[0]);
  } catch (error) {
    console.error('PUT librarian error:', error);
    return NextResponse.json({ error: 'Failed to update librarian' }, { status: 500 });
  }
}

export async function DELETE(
  request: NextRequest,
  { params }: { params: Promise<{ id: string }> }
) {
  try {
    const { id } = await params;
    
    const result = await query('DELETE FROM librarians_5900 WHERE id = $1 RETURNING *', [id]);
    
    if (result.rows.length === 0) {
      return NextResponse.json({ error: 'Librarian not found' }, { status: 404 });
    }
    
    return NextResponse.json({ message: 'Librarian deleted successfully' });
  } catch (error) {
    console.error('DELETE librarian error:', error);
    return NextResponse.json({ error: 'Failed to delete librarian' }, { status: 500 });
  }
}
