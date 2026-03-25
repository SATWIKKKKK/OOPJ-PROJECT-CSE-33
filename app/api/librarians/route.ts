import { NextRequest, NextResponse } from 'next/server';
import { query } from '@/lib/db';

export async function GET(request: NextRequest) {
  try {
    const result = await query('SELECT * FROM librarians_5900 ORDER BY name ASC');
    return NextResponse.json(result.rows);
  } catch (error) {
    console.error('GET librarians error:', error);
    return NextResponse.json({ error: 'Failed to fetch librarians' }, { status: 500 });
  }
}

export async function POST(request: NextRequest) {
  try {
    const { name, email, phone, employee_id } = await request.json();
    
    const result = await query(
      `INSERT INTO librarians_5900 (name, email, phone, employee_id, hire_date)
       VALUES ($1, $2, $3, $4, CURRENT_DATE)
       RETURNING *`,
      [name, email, phone, employee_id]
    );
    
    return NextResponse.json(result.rows[0], { status: 201 });
  } catch (error) {
    console.error('POST librarians error:', error);
    return NextResponse.json({ error: 'Failed to create librarian' }, { status: 500 });
  }
}
