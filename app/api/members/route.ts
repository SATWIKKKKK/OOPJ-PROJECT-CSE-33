import { NextRequest, NextResponse } from 'next/server';
import { query } from '@/lib/db';

export async function GET(request: NextRequest) {
  try {
    const result = await query('SELECT * FROM members_5900 ORDER BY name ASC');
    return NextResponse.json(result.rows);
  } catch (error) {
    console.error('GET members error:', error);
    return NextResponse.json({ error: 'Failed to fetch members' }, { status: 500 });
  }
}

export async function POST(request: NextRequest) {
  try {
    const { name, email, phone, address } = await request.json();
    
    const result = await query(
      `INSERT INTO members_5900 (name, email, phone, address, membership_date)
       VALUES ($1, $2, $3, $4, CURRENT_DATE)
       RETURNING *`,
      [name, email, phone, address]
    );
    
    return NextResponse.json(result.rows[0], { status: 201 });
  } catch (error) {
    console.error('POST members error:', error);
    return NextResponse.json({ error: 'Failed to create member' }, { status: 500 });
  }
}
