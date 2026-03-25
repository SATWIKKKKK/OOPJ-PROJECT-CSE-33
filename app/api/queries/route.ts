import { NextRequest, NextResponse } from 'next/server';
import { query } from '@/lib/db';

export async function GET(request: NextRequest) {
  const searchParams = request.nextUrl.searchParams;
  const q = searchParams.get('q');

  try {
    let result;

    switch (q) {
      // Q2: List all books with available copies greater than a certain number
      case 'available-books':
        result = await query(
          `SELECT id, title, author, available_copies 
           FROM books_5900 
           WHERE available_copies > 0 
           ORDER BY title ASC`
        );
        break;

      // Q3: Find all members who have not returned books on time
      case 'overdue-members':
        result = await query(
          `SELECT DISTINCT m.id, m.name, m.email, COUNT(ir.id) as overdue_count
           FROM members_5900 m
           JOIN issue_records_5900 ir ON m.id = ir.member_id
           WHERE ir.return_date IS NULL AND ir.due_date < CURRENT_DATE
           GROUP BY m.id, m.name, m.email
           ORDER BY m.name ASC`
        );
        break;

      // Q4: Count total number of books_5900 issued by each librarian
      case 'librarian-issues':
        result = await query(
          `SELECT l.id, l.name, COUNT(ir.id) as total_issued
           FROM librarians_5900 l
           LEFT JOIN issue_records_5900 ir ON l.id = ir.librarian_id
           GROUP BY l.id, l.name
           ORDER BY total_issued DESC`
        );
        break;

      // Q5: Find the member who has issued the most books_5900
      case 'most-active-member':
        result = await query(
          `SELECT m.id, m.name, m.email, COUNT(ir.id) as books_issued
           FROM members_5900 m
           JOIN issue_records_5900 ir ON m.id = ir.member_id
           GROUP BY m.id, m.name, m.email
           ORDER BY books_issued DESC
           LIMIT 1`
        );
        break;

      // Q6: List books with total copies greater than 2
      case 'popular-books':
        result = await query(
          `SELECT id, title, author, total_copies, available_copies
           FROM books_5900
           WHERE total_copies > 2
           ORDER BY total_copies DESC`
        );
        break;

      // Q7: Find members whose membership date is after a specific date
      case 'recent-members':
        result = await query(
          `SELECT id, name, email, membership_date
           FROM members_5900
           WHERE membership_date > CURRENT_DATE - INTERVAL '6 months'
           ORDER BY membership_date DESC`
        );
        break;

      // Q8: Book issue summary view
      case 'issue-summary':
        result = await query('SELECT * FROM current_issued_books');
        break;

      // Q9: Member activity summary view
      case 'member-summary':
        result = await query('SELECT * FROM books_summary');
        break;

      // Q10: Books issued in a specific month
      case 'issues-by-month':
        result = await query(
          `SELECT DATE_TRUNC('month', ir.issue_date) as month, 
                  COUNT(*) as total_issues
           FROM issue_records_5900 ir
           GROUP BY DATE_TRUNC('month', ir.issue_date)
           ORDER BY month DESC`
        );
        break;

      // Q11: Librarians with zero issues
      case 'inactive-librarians':
        result = await query(
          `SELECT l.id, l.name, l.employee_id
           FROM librarians_5900 l
           WHERE NOT EXISTS (
             SELECT 1 FROM issue_records_5900 ir WHERE ir.librarian_id = l.id
           )
           ORDER BY l.name ASC`
        );
        break;

      // Q12: Average books per member
      case 'avg-books-per-member':
        result = await query(
          `SELECT AVG(book_count) as avg_books_per_member
           FROM (
             SELECT COUNT(*) as book_count
             FROM members_5900 m
             JOIN issue_records_5900 ir ON m.id = ir.member_id
             GROUP BY m.id
           ) subquery`
        );
        break;

      // Q13: Most issued book
      case 'most-issued-book':
        result = await query(
          `SELECT b.id, b.title, b.author, COUNT(ir.id) as times_issued
           FROM books_5900 b
           LEFT JOIN issue_records_5900 ir ON b.id = ir.book_id
           GROUP BY b.id, b.title, b.author
           ORDER BY times_issued DESC
           LIMIT 1`
        );
        break;

      // Q14: Members with active issues
      case 'active-issues':
        result = await query(
          `SELECT DISTINCT m.id, m.name, m.email, COUNT(ir.id) as active_issues
           FROM members_5900 m
           JOIN issue_records_5900 ir ON m.id = ir.member_id
           WHERE ir.return_date IS NULL
           GROUP BY m.id, m.name, m.email
           ORDER BY active_issues DESC`
        );
        break;

      // Q15: Book inventory summary
      case 'inventory-summary':
        result = await query(
          `SELECT 
             COUNT(*) as total_titles,
             SUM(total_copies) as total_copies,
             SUM(available_copies) as total_available
           FROM books_5900`
        );
        break;

      default:
        return NextResponse.json({ error: 'Invalid query parameter' }, { status: 400 });
    }

    return NextResponse.json(result.rows);
  } catch (error) {
    console.error('Query error:', error);
    return NextResponse.json({ error: 'Failed to execute query' }, { status: 500 });
  }
}
