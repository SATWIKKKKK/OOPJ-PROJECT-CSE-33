import fs from 'node:fs/promises';

const base = 'http://localhost:3005';
const outPath = new URL('../e2e-result.json', import.meta.url);

async function hit(method, route, body) {
  const res = await fetch(base + route, {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: body ? JSON.stringify(body) : undefined,
  });

  const text = await res.text();
  let data;
  try {
    data = text ? JSON.parse(text) : null;
  } catch {
    data = text;
  }

  return { status: res.status, data };
}

async function run() {
  const report = [];

  // Frontend pages
  for (const page of ['/', '/books', '/members', '/librarians', '/issues', '/queries', '/views']) {
    const res = await fetch(base + page);
    report.push({ type: 'frontend', route: page, status: res.status });
  }

  // Seed baseline data
  report.push({ type: 'api', route: 'POST /api/setup', ...(await hit('POST', '/api/setup')) });

  // CRUD tests
  const bookPayload = {
    title: 'E2E Database Systems',
    author: 'Test Author',
    isbn: '9780000005901',
    publication_year: 2025,
    total_copies: 5,
  };
  const memberPayload = {
    name: 'E2E Member',
    email: 'e2e.member@example.com',
    phone: '9000000000',
    address: 'E2E Street',
  };
  const librarianPayload = {
    name: 'E2E Librarian',
    email: 'e2e.librarian@example.com',
    phone: '9111111111',
    employee_id: 'LIB-E2E-5900',
  };

  const createdBook = await hit('POST', '/api/books', bookPayload);
  const createdMember = await hit('POST', '/api/members', memberPayload);
  const createdLibrarian = await hit('POST', '/api/librarians', librarianPayload);

  report.push({ type: 'api', route: 'POST /api/books', ...createdBook });
  report.push({ type: 'api', route: 'POST /api/members', ...createdMember });
  report.push({ type: 'api', route: 'POST /api/librarians', ...createdLibrarian });

  const bookId = createdBook.data?.id;
  const memberId = createdMember.data?.id;
  const librarianId = createdLibrarian.data?.id;

  report.push({ type: 'api', route: 'GET /api/books', ...(await hit('GET', '/api/books')) });
  report.push({ type: 'api', route: 'GET /api/members', ...(await hit('GET', '/api/members')) });
  report.push({ type: 'api', route: 'GET /api/librarians', ...(await hit('GET', '/api/librarians')) });

  if (bookId) {
    report.push({
      type: 'api',
      route: `PUT /api/books/${bookId}`,
      ...(await hit('PUT', `/api/books/${bookId}`, {
        title: 'E2E Database Systems Revised',
        author: 'Test Author',
        isbn: '9780000005901',
        publication_year: 2026,
        total_copies: 6,
        available_copies: 6,
      })),
    });
  }

  if (memberId) {
    report.push({
      type: 'api',
      route: `PUT /api/members/${memberId}`,
      ...(await hit('PUT', `/api/members/${memberId}`, {
        name: 'E2E Member Updated',
        email: 'e2e.member.updated@example.com',
        phone: '9222222222',
        address: 'E2E Updated Street',
      })),
    });
  }

  if (librarianId) {
    report.push({
      type: 'api',
      route: `PUT /api/librarians/${librarianId}`,
      ...(await hit('PUT', `/api/librarians/${librarianId}`, {
        name: 'E2E Librarian Updated',
        email: 'e2e.librarian.updated@example.com',
        phone: '9333333333',
        employee_id: 'LIB-E2E-5901',
      })),
    });
  }

  let issueId;
  if (bookId && memberId && librarianId) {
    const createdIssue = await hit('POST', '/api/issues', {
      book_id: bookId,
      member_id: memberId,
      librarian_id: librarianId,
      due_date: '2026-12-31',
    });
    issueId = createdIssue.data?.id;
    report.push({ type: 'api', route: 'POST /api/issues', ...createdIssue });
  }

  report.push({ type: 'api', route: 'GET /api/issues', ...(await hit('GET', '/api/issues')) });

  if (issueId) {
    report.push({
      type: 'api',
      route: `PUT /api/issues/${issueId}`,
      ...(await hit('PUT', `/api/issues/${issueId}`, {
        return_date: '2026-04-01',
        status: 'returned',
      })),
    });
  }

  const queryCases = [
    'available-books',
    'overdue-members',
    'librarian-issues',
    'most-active-member',
    'popular-books',
    'recent-members',
    'issue-summary',
    'member-summary',
    'issues-by-month',
    'inactive-librarians',
    'avg-books-per-member',
    'most-issued-book',
    'active-issues',
    'inventory-summary',
  ];

  for (const q of queryCases) {
    report.push({
      type: 'api',
      route: `GET /api/queries?q=${q}`,
      ...(await hit('GET', `/api/queries?q=${q}`)),
    });
  }

  if (issueId) report.push({ type: 'api', route: `DELETE /api/issues/${issueId}`, ...(await hit('DELETE', `/api/issues/${issueId}`)) });
  if (bookId) report.push({ type: 'api', route: `DELETE /api/books/${bookId}`, ...(await hit('DELETE', `/api/books/${bookId}`)) });
  if (memberId) report.push({ type: 'api', route: `DELETE /api/members/${memberId}`, ...(await hit('DELETE', `/api/members/${memberId}`)) });
  if (librarianId) report.push({ type: 'api', route: `DELETE /api/librarians/${librarianId}`, ...(await hit('DELETE', `/api/librarians/${librarianId}`)) });

  const failed = report.filter((r) => r.status < 200 || r.status >= 300);
  const summary = { total: report.length, failed: failed.length, failedRoutes: failed, report };
  await fs.writeFile(outPath, JSON.stringify(summary, null, 2), 'utf8');

  if (failed.length > 0) process.exit(2);
}

run().catch(async (err) => {
  await fs.writeFile(outPath, JSON.stringify({ fatal: String(err) }, null, 2), 'utf8');
  process.exit(1);
});
