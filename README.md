# Library Management System

A comprehensive full-stack library management and inventory system built with Next.js, React, and PostgreSQL.

## Overview

This application provides complete management capabilities for a library including:
- **Book Management**: Manage library inventory with CRUD operations
- **Member Management**: Register and track library members
- **Librarian Management**: Manage staff accounts and permissions
- **Issue Management**: Track book circulation and returns
- **Advanced Queries**: 14+ analytical queries for reporting
- **Database Views**: Materialized views for performance optimization

## Technology Stack

- **Frontend**: Next.js 16, React 19, TypeScript, Tailwind CSS
- **Backend**: Next.js API Routes, Node.js
- **Database**: PostgreSQL (Neon serverless)
- **UI Components**: shadcn/ui
- **Fonts**: Playfair Display (headings), IBM Plex Mono (data)
- **Package Manager**: pnpm

## Features

### Database Schema
- **Tables**: books, members, librarians, issue_records
- **Views**: book_issue_summary, member_activity_summary
- **Triggers**: Automatic inventory sync on issue creation
- **Indexes**: Optimized queries for performance

### API Endpoints (19 Total)

#### Setup
- `POST /api/setup` - Initialize database with sample data

#### Books (CRUD)
- `GET /api/books` - List all books
- `POST /api/books` - Create new book
- `GET /api/books/[id]` - Get book details
- `PUT /api/books/[id]` - Update book
- `DELETE /api/books/[id]` - Delete book

#### Members (CRUD)
- `GET /api/members` - List all members
- `POST /api/members` - Register new member
- `GET /api/members/[id]` - Get member details
- `PUT /api/members/[id]` - Update member
- `DELETE /api/members/[id]` - Delete member

#### Librarians (CRUD)
- `GET /api/librarians` - List all librarians
- `POST /api/librarians` - Hire new librarian
- `GET /api/librarians/[id]` - Get librarian details
- `PUT /api/librarians/[id]` - Update librarian
- `DELETE /api/librarians/[id]` - Delete librarian

#### Issues (CRUD)
- `GET /api/issues` - List all issues
- `POST /api/issues` - Issue new book
- `GET /api/issues/[id]` - Get issue details
- `PUT /api/issues/[id]` - Mark book returned
- `DELETE /api/issues/[id]` - Delete issue

#### Queries (14 Reports)
- `GET /api/queries?q=available-books` - Available inventory (Q2)
- `GET /api/queries?q=overdue-members` - Overdue returns (Q3)
- `GET /api/queries?q=librarian-issues` - Librarian statistics (Q4)
- `GET /api/queries?q=most-active-member` - Top borrowers (Q5)
- `GET /api/queries?q=popular-books` - High-copy books (Q6)
- `GET /api/queries?q=recent-members` - New registrations (Q7)
- `GET /api/queries?q=issue-summary` - View: Issue summary (Q8)
- `GET /api/queries?q=member-summary` - View: Member activity (Q9)
- `GET /api/queries?q=issues-by-month` - Monthly statistics (Q10)
- `GET /api/queries?q=inactive-librarians` - Unused accounts (Q11)
- `GET /api/queries?q=avg-books-per-member` - Usage metrics (Q12)
- `GET /api/queries?q=most-issued-book` - Top circulation (Q13)
- `GET /api/queries?q=active-issues` - Current borrows (Q14)
- `GET /api/queries?q=inventory-summary` - Total inventory (Q15)

## Setup Instructions

### 1. Prerequisites
- Node.js 18+ and pnpm
- PostgreSQL database (Neon recommended for serverless)

### 2. Clone and Install
```bash
git clone <repository-url>
cd library-management
pnpm install
```

### 3. Database Setup
```bash
# Copy environment template
cp .env.example .env.local

# Update .env.local with your Neon DATABASE_URL
# Format: postgresql://user:password@host/database
```

### 4. Initialize Database
```bash
# Run migrations
pnpm run setup-db

# Or manually execute SQL scripts
# scripts/01-schema.sql
# scripts/02-views-triggers.sql
```

### 5. Seed Initial Data
```bash
curl -X POST http://localhost:3000/api/setup
```

### 6. Start Development Server
```bash
pnpm run dev
```

Visit `http://localhost:3000` in your browser.

## Project Structure

```
app/
├── api/
│   ├── setup/              # Setup endpoint
│   ├── books/              # Book CRUD
│   ├── members/            # Member CRUD
│   ├── librarians/         # Librarian CRUD
│   ├── issues/             # Issue CRUD
│   └── queries/            # Analytics queries
├── books/                  # Books page
├── members/                # Members page
├── librarians/             # Librarians page
├── issues/                 # Issues page
├── queries/                # Queries page
├── views/                  # Database views page
├── layout.tsx              # Root layout
├── page.tsx                # Dashboard
└── globals.css             # Tailwind styles

lib/
├── db.ts                   # Database utilities

scripts/
├── 01-schema.sql           # Core tables
└── 02-views-triggers.sql   # Views and triggers

components/
└── ui/                     # shadcn/ui components
```

## Database Schema

### Books Table
```sql
books (
  id SERIAL PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  author VARCHAR(255) NOT NULL,
  isbn VARCHAR(20) UNIQUE NOT NULL,
  publication_year INTEGER,
  total_copies INTEGER DEFAULT 1,
  available_copies INTEGER DEFAULT 1
)
```

### Members Table
```sql
members (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  phone VARCHAR(20),
  address TEXT,
  membership_date TIMESTAMP DEFAULT NOW()
)
```

### Librarians Table
```sql
librarians (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  phone VARCHAR(20),
  employee_id VARCHAR(20) UNIQUE NOT NULL,
  hire_date TIMESTAMP DEFAULT NOW()
)
```

### Issue Records Table
```sql
issue_records (
  id SERIAL PRIMARY KEY,
  book_id INTEGER REFERENCES books(id),
  member_id INTEGER REFERENCES members(id),
  librarian_id INTEGER REFERENCES librarians(id),
  issue_date TIMESTAMP DEFAULT NOW(),
  due_date TIMESTAMP NOT NULL,
  return_date TIMESTAMP
)
```

## Styling

The application uses a dark editorial theme with:
- **Primary Color**: Amber/Gold (#d4a574) for accents and highlights
- **Background**: Deep charcoal (#0f0d0a)
- **Foreground**: Cream (#f5f1e6)
- **Fonts**: Playfair Display for elegant headings, IBM Plex Mono for data

## Deployment

### Deploy to Vercel
```bash
npm install -g vercel
vercel
```

### Environment Variables on Vercel
1. Go to Project Settings → Environment Variables
2. Add `DATABASE_URL` with your Neon PostgreSQL connection string
3. Redeploy

## Development

### Add New Query
1. Add endpoint to `app/api/queries/route.ts`
2. Add query selector in `app/queries/page.tsx`
3. Test via `GET /api/queries?q=your-query-id`

### Modify Styling
- Update CSS variables in `app/globals.css`
- Customize Tailwind classes in component files

### Database Changes
1. Create new SQL script in `scripts/`
2. Execute via `pnpm run execute-sql scripts/03-new-migration.sql`
3. Update database utilities if needed

## Performance Considerations

- **Database Views**: Pre-computed for analytics queries
- **Triggers**: Automatic inventory updates on issue creation
- **Indexes**: Optimized for common queries
- **Caching**: Consider adding Redis for frequently accessed data
- **Pagination**: Implement for large result sets (future enhancement)

## Future Enhancements

- [ ] Authentication and authorization
- [ ] Advanced search and filtering
- [ ] Pagination for large datasets
- [ ] Book cover images
- [ ] Email notifications for overdue books
- [ ] Export to CSV/PDF
- [ ] Advanced analytics dashboard
- [ ] Multi-branch support

## License

MIT

## Support

For issues or questions, please open a GitHub issue or contact the development team.
